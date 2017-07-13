package qdos;

import m68k.memory._
import m68k.cpu.{ Cpu, MC68000 }
import m68k.Monitor;

import java.io.{ IOException, InputStream, FileInputStream }

class QDOSMonitor(ramSize: Int = 128, romFile: InputStream) {
  val rom = new InputStreamAddressSpace(romFile, 0)

  // http://www.dilwyn.me.uk/docs/ebooks/olqlug/QL%20Manual%20-%20Concepts.htm#memorymap
  val ram = new MemorySpace(ramSize, 0x20000)

  val addrSpace = new LinkedAddressSpace(rom,
    new LinkedAddressSpace(ram,
      new NullAddressSpace(ram.getEndAddress + 1)))

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
