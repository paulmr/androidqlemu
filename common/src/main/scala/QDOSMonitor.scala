package qdos

import scala.concurrent.{ ExecutionContext, Future }

import m68k.memory._
import qdos.io._
import m68k.cpu.MC68000
import m68k.cpu.Cpu
import m68k.{ Monitor, MonitorCallback };

import m68k.memory.syntax._

import java.io.{ InputStream, FileInputStream }

import smsqmulator.util.Logger.log

import java.io.{ InputStream, OutputStream, PrintStream }

class QDOSMonitor(
  ramSize: Int = 128,
  romFile: InputStream,
  promFile: Option[InputStream] = None,
  inputStream: InputStream = System.in,
  outputStream: OutputStream = System.out
) {
  protected var running = false

  def isRunning = running

  // http://www.dilwyn.me.uk/docs/ebooks/olqlug/QL%20Manual%20-%20Concepts.htm#memorymap
  val rom  = new InputStreamAddressSpace(romFile, 0)

  val prom = promFile.map(in => new InputStreamAddressSpace(in, 0xC000))
    .getOrElse(new PromAddressSpace(this, 0xC000, "QLEMU010").init())

  val ram  = new MemorySpace            (ramSize, 0x20000)

  val ipc = new IPC(this)
  val io   = new IOAddressSpace         (0x10000, 0x20000 - 1, this, ipc)

  def addKey(k: Int) = {
    ipc.addKey(k)
    io.addInterrupt(2)
    cpu.raiseInterrupt(2)
  }

  private var tickerC = 0
  lazy val ticker = {
    val t = smsqmulator.util.Ticker.fiftyHz { () =>
      tickerC += 1
      if(tickerC % 100 == 0) log(s"[PMR 1658] Ticker $tickerC")
      io.addInterrupt(2)
      cpu.raiseInterrupt(2)
    }
    //t.start()
    t
  }

  private var breaks = Vector.empty[Int]

  private var time = 0
  def getTime = time

  val addrSpace = rom ~> prom ~> io ~> ram

  log(f"Memory: ${addrSpace.getStartAddress}%08x => ${addrSpace.getEndAddress}%08x")

  lazy val cpu = new MC68000()
  cpu.setAddressSpace(addrSpace)
  cpu.reset()

  def doCommand(s: String): Unit = log("UNIMPLEMENTED")

  /* this behaves the same as the QDOS trap IO.QIN and allows us to
   * insert data into the queues, e.g. the keyboard queue. */
  def enqueue(addr: Int, value: Int) = {
    val eof = cpu.readMemoryByte(addr)
    if(eof == 0) {
      var writeAddr = cpu.readMemoryLong(addr + 0x8)
      cpu.writeMemoryByte(writeAddr, value)
      writeAddr += 1
      if(writeAddr > cpu.readMemoryLong(addr + 0x4))
        writeAddr = cpu.readMemoryLong(addr + 0x10) // reset to start of queue
      cpu.writeMemoryLong(addr + 0x8, writeAddr)
    }
  }

  def sysVar(offset: Int, size: MemSize.Value): Int = {
    val op = size match {
      case MemSize.BYTE => cpu.readMemoryByte _
      case MemSize.WORD => cpu.readMemoryWord _
      case MemSize.LONG => cpu.readMemoryLong _
    }
    op(QDOSMonitor.SYS_VAR_BASE + offset)
  }

  def sysVar(sv: QDOSMonitor.SysVar): Int = {
    sysVar(sv.offset, sv.size)
  }

  def setSysVar(sysVar: QDOSMonitor.SysVar, value: Int): Unit = {
    val op = sysVar.size match {
      case MemSize.BYTE => cpu.writeMemoryByte _
      case MemSize.WORD => cpu.writeMemoryWord _
      case MemSize.LONG => cpu.writeMemoryLong _
    }
    op(QDOSMonitor.SYS_VAR_BASE + sysVar.offset, value)
  }

  def toggleRunState() = {
    running = !running
    isRunning
  }

  def stop = running = false

  def step = time += cpu.execute

  def execute(implicit ec: ExecutionContext) = {
    running = true
    // ticker.unpause
    Future {
      log("Running ...")
      while(isRunning && !hasBreak(cpu.getPC)) step
      running = false // needed if stopped by break
      log("Not running")
      ticker.pause
    }
  }

  def reset = {
    cpu.reset
    time = 0
  }

  def getBreaks: List[Int] = breaks.toList

  def hasBreak(addr: Int) = breaks.contains(addr)

  def addBreak(addr: Int) =
    if(!breaks.contains(addr)) breaks = breaks :+ addr

  def delBreak(addr: Int) =
    breaks = breaks.filter(_ == addr)

  def clearBreaks =
    breaks = Vector.empty[Int]

  def shutdown(): Unit = ()

}

object QDOSMonitor {
  import qdos.MemSize

  val SYS_VAR_BASE = 0x28000

  sealed trait SysVar {
    def offset: Int
    def size: MemSize.Value
  }
  object SV {
    case object DDLST extends SysVar {
      val offset = 0x48
      val size = MemSize.LONG
    }
    case object KEYQ extends SysVar {
      val offset = 0x4c
      val size = MemSize.LONG
    }
  }
}
