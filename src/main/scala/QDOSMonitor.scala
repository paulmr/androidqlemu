package qdos

import m68k.memory._
import m68k.cpu.{ Cpu, MC68000 }
import m68k.Monitor;

import m68k.memory.syntax._

import java.io.{ IOException, InputStream, FileInputStream }

class QDOSMonitor(ramSize: Int = 128, romFile: InputStream) {
  // http://www.dilwyn.me.uk/docs/ebooks/olqlug/QL%20Manual%20-%20Concepts.htm#memorymap
  val rom = new InputStreamAddressSpace(romFile, 0)
  val ram = new MemorySpace            (ramSize, 0x20000)
  val io  = new MemorySpace            (32,      0x18000)

  private var breaks = Vector.empty[Int]

  private var time = 0
  def getTime = time

  val addrSpace = rom ~> io ~> ram

  println(f"Memory: ${addrSpace.getStartAddress}%08x => ${addrSpace.getEndAddress}%08x")

  val cpu = new MC68000()
  cpu.setAddressSpace(addrSpace)
  cpu.reset()

  lazy val getMonitor = new Monitor(cpu, addrSpace)

  def step = {
    time += cpu.execute
  }

  def execute =
    while(!hasBreak(cpu.getPC)) step

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
  def main(args: Array[String]) = {
    val q = new QDOSMonitor(
      romFile = new FileInputStream(args.headOption.getOrElse("rom/js.rom"))
    )
    q.getMonitor.run()
  }
}
