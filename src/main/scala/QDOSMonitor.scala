package qdos;

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

  val addrSpace = rom ~> io ~> ram

  println(f"Memory: ${addrSpace.getStartAddress}%08x => ${addrSpace.getEndAddress}%08x")

  val cpu = new MC68000()
  cpu.setAddressSpace(addrSpace)
  cpu.reset()

  def doMonitor = {
    new Monitor(cpu, addrSpace).run()
  }
}

object QDOSMonitor {
  def main(args: Array[String]) = {
    val q = new QDOSMonitor(
      romFile = new FileInputStream(args.headOption.getOrElse("rom/js.rom"))
    )
    q.doMonitor
  }
}
