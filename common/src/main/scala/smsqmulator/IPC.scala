package qdos

import smsqmulator.util.Logger.log

/**
  * the logic here has been stolen from qlay's qlio.c (and translated
  * into scala). The copyright notice for QLAY is:
  *
  * "1. Copyrights
  * This program is freeware. You may do with it whatever you want for
  * personal use. Copyright Jan Venema.
  *
  * Permission is granted to redistribute this program free of charge,
  * provided it is distributed in the full archive with unmodified contents.
  * No profit beyond the price of the media on which it is distributed is
  * made."
  *
  *  Big thanks to Jan Venema.
  */

class IPC {

  private var IPCrcvd = 1	/* bit marker */
  private var IPCcmd = 0

  private var IPCcnt = 0

  private var IPCwfc = true // waiting for command
  private var IPC020 = 0
  private var IPCreturn = 0

  private def exec_IPCcmd(cmd: Int) = {
    log(s"Would execute ipc cmd $cmd")
    IPCreturn = 0xFF
    IPCcnt = 8
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
