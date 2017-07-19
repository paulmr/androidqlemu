package qdos

import scala.concurrent.{ ExecutionContext, Future }

import m68k.memory._
import m68k.cpu.MC68000
import m68k.Monitor;

import m68k.memory.syntax._

import java.io.{ InputStream, FileInputStream }

class QDOSMonitor(ramSize: Int = 128, romFile: InputStream, promFile: Option[InputStream] = None) {
  protected var running = false

  def isRunning = running

  // http://www.dilwyn.me.uk/docs/ebooks/olqlug/QL%20Manual%20-%20Concepts.htm#memorymap
  val rom  = new InputStreamAddressSpace(romFile, 0)
  val prom = promFile match {
    case None => new NullAddressSpace       (0xC000,  0x18000 - 1)
    case Some(prom) => new InputStreamAddressSpace(prom, 0xC000)
  }
  val ram  = new MemorySpace            (ramSize, 0x20000)
  val io   = new MemorySpace            (32,      0x18000)

  private var breaks = Vector.empty[Int]

  private var time = 0
  def getTime = time

  val addrSpace = rom ~> prom ~> io ~> ram

  println(f"Memory: ${addrSpace.getStartAddress}%08x => ${addrSpace.getEndAddress}%08x")

  val cpu = new MC68000()
  cpu.setAddressSpace(addrSpace)
  cpu.reset()

  lazy val getMonitor = new Monitor(cpu, addrSpace)

  def stop = running = false

  def step = {
    time += cpu.execute
  }

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
}

object QDOSMonitor {
  def parseInt(value: String) =
    if(value.startsWith("$"))
      (java.lang.Long.parseLong(value.substring(1), 16) & 0x0ffffffffL).toInt
    else
      (java.lang.Long.parseLong(value) & 0x0ffffffffL).toInt

  def main(args: Array[String]) = {
    val q = new QDOSMonitor(
      romFile = new FileInputStream(args.headOption.getOrElse("rom/js.rom"))
    )
    q.getMonitor.run()
  }
}
