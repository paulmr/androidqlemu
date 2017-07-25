package qdos

import scala.concurrent.{ ExecutionContext, Future }

import m68k.memory._
import qdos.io._
import m68k.cpu.MC68000
import m68k.cpu.Cpu
import m68k.{ Monitor, MonitorCallback };

import m68k.memory.syntax._

import java.io.{ InputStream, FileInputStream }

import smsqmulator.util.Logger

import java.io.{ InputStream, OutputStream, PrintStream }
import tcl.lang.{ StdChannel, Interp }

class QDOSMonitor(ramSize: Int = 128, romFile: InputStream, promFile: Option[InputStream] = None, inputStream: InputStream = System.in, outputStream: OutputStream = System.out) {
  protected var running = false

  def isRunning = running

  // http://www.dilwyn.me.uk/docs/ebooks/olqlug/QL%20Manual%20-%20Concepts.htm#memorymap
  val rom  = new InputStreamAddressSpace(romFile, 0)
  val prom = promFile match {
    case None => new NullAddressSpace       (0xC000,  0x10000 - 1)
    case Some(prom) => new InputStreamAddressSpace(prom, 0xC000)
  }
  val ram  = new MemorySpace            (ramSize, 0x20000)

  // attach behaviour to addresses within the IO block
  val ioControl: Map[Int, IOControl] = Map(
    0x18002 -> new IOControl with IOTransmit with IOReadNull
  )

  val io   = new IOAddressSpace         (0x10000, 0x20000 - 1, ioControl)

  private var breaks = Vector.empty[Int]

  private var time = 0
  def getTime = time

  val addrSpace = rom ~> prom ~> io ~> ram

  println(f"Memory: ${addrSpace.getStartAddress}%08x => ${addrSpace.getEndAddress}%08x")

  val cpu = new MC68000()
  cpu.setAddressSpace(addrSpace)
  cpu.reset()


  StdChannel.setIn(inputStream)
  StdChannel.setOut(new PrintStream(outputStream))

  protected lazy val jacl = new Interp()
  jacl.eval("puts {Hello world (jacl)!}")

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

  val SYS_VAR_BASE = 0x28000

  def sysVar(offset: Int, size: Int): Int =
    size match {
      case 1 => cpu.readMemoryByte(SYS_VAR_BASE + offset)
      case 2 => cpu.readMemoryWord(SYS_VAR_BASE + offset)
      case _ => cpu.readMemoryLong(SYS_VAR_BASE + offset)
    }

  def sysVar(s: String): Int = {
    val (offset, size) = sysVarTbl(s)
    sysVar(offset, size)
  }

  def sysVar_CUR_KEY_QUEUE = sysVar("SV_KEYQ")

  // sysvar names to offset + size
  val sysVarTbl = Map(
    "SV_KEYQ" -> (0x4c, 4)
  )

  def toggleRunState() = {
    running = !running
    isRunning
  }

  def stop = running = false

  def step = time += cpu.execute

  def execute(implicit ec: ExecutionContext) = {
    running = true
    Future {
      while(isRunning && !hasBreak(cpu.getPC)) step
      running = false // needed if stopped by break
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

  def shutdown(): Unit =
    jacl.dispose()

}

object QDOSMonitor {
  def parseInt(value: String) =
    if(value.startsWith("$"))
      (java.lang.Long.parseLong(value.substring(1), 16) & 0x0ffffffffL).toInt
    else
      (java.lang.Long.parseLong(value) & 0x0ffffffffL).toInt

  def main(args: Array[String]) = {

    Logger.setCb(println _)

    val q = new QDOSMonitor(
      romFile = new FileInputStream(args.headOption.getOrElse("rom/js.rom")),
      promFile = Some(new FileInputStream("src/main/res/raw/qlemurom"))
    )
    q.shutdown()
  }
}
