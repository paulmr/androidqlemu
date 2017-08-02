package qdos

import smsqmulator.util.Logger.log

/**
  * the logic here has been stolen from qlay's qlio.c (and translated
  * into scala) as downloaded from http://web.inter.nl.net/hcc/A.Jaw.Venema/.
  * 
  * The copyright notice for QLAY is as follows, which I take to mean
  * that this is ok to do!:
  *
  *    "1. Copyrights This program is freeware. You may do with it
  *    whatever you want for personal use. Copyright Jan Venema.
  *   
  *    Permission is granted to redistribute this program free of
  *    charge, provided it is distributed in the full archive with
  *    unmodified contents.  No profit beyond the price of the media
  *    on which it is distributed is made."
  *
  * So big thanks to Jan Venema for saving me a lot of time trying to
  * work out how the very complicated IPC protocol works.
  */

class IPC(mon: QDOSMonitor) {

  private var IPCrcvd = 1	/* bit marker */
  private var IPCcmd = 0

  private var IPCcnt = 0

  private var IPCwfc = true // waiting for command
  private var IPC020 = 0
  private var IPCreturn = 0

  def makeKey(col: Int, row: Int) = (col << 3) | row


  var keyBufferHead = 0
  val keyBuffer = Array.fill(0)(0)

  def addKey(k: Int) = {
    if(keyBufferHead < keyBuffer.size) {
      keyBuffer(keyBufferHead) = k
      keyBufferHead += 1
    }
  }

  private def exec_IPCcmd(cmd: Int) = {
    cmd match {
      case 1 =>
        IPCreturn = 0 | (if(keyBufferHead > 0) 0x01 else 0)
        IPCcnt = 8
      case 8 => // get key
        if(keyBufferHead > 0) {
          keyBufferHead = 0
          IPCreturn = keyBuffer(0) | 0x1000
          IPCcnt = 16
        } else {
          IPCreturn = 0
        }
      case _ =>
        IPCreturn = 0
        IPCcnt = 0
    }
    log(f"exec_IPCcmd: $cmd (ipcreturn: $IPCreturn%x)")
  }

  def send(data: Int): Unit = {
    if(IPCwfc) {
      if ((data&0x0c)==0x0c) {
        IPCrcvd<<=1;
        IPCrcvd |= (if(data==0x0c) 0 else 1)
        if ((IPCrcvd&0x10)==0x10) {
	  IPCcmd=IPCrcvd&0x0f;
	  IPCrcvd=1;
	  IPCwfc=false;
	  exec_IPCcmd(IPCcmd);
        }
      }
    } else {
      /* expect 0x0e */
      if (data!=0x0e) {
        log(f"ERRORIPC?:$data%x")
        return
      }

      IPC020=0; /*970718*/
      IPCcnt -= 1;

      if((IPCreturn & (1<<IPCcnt)) != 0) IPC020|=0x80 ;

      IPC020<<=8;	/* will be read as byte twice, sender will shift back */
      /* lower 16 bit have real value now, put in an end indicator */
      IPC020|=0xa50000;

      if (IPCcnt==0) {/* this command session done */
        log(s"IPC finished sending $IPCreturn")
	IPCwfc=true; /* wait for next command */
      }
    }
  }

  def receive(): Int = {
    if (IPC020!=0) {
      val t = IPC020;
      IPC020>>=8;
      if(IPC020==0xa5)IPC020=0; /* clear end marker */
      t & 0xFF;
    } else {
      // ???
      0
    }
  }

}
