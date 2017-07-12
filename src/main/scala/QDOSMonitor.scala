package qdos;

import m68k.memory._
import m68k.cpu.{ Cpu, MC68000 }
import m68k.Monitor;

import java.io.{ IOException, FileInputStream }

object QDOSMonitor extends App {
  val ramSize = 128 // kilobytes => unexpanded QL
  
  val fin = new FileInputStream(args.headOption.getOrElse("js.rom"))
  
  val rom = new ROMAddressSpace(fin.getChannel(), 0)

  // http://www.dilwyn.me.uk/docs/ebooks/olqlug/QL%20Manual%20-%20Concepts.htm#memorymap
  val ram = new MemorySpace(ramSize, 0x20000)

  val addrSpace = new LinkedAddressSpace(rom,
    new LinkedAddressSpace(ram,
      new NullAddressSpace(ram.getEndAddress + 1)))

  println(f"ROM: ${rom.getStartAddress}%08x => ${rom.getEndAddress}%08x")
  println(f"RAM: ${ram.getStartAddress}%08x => ${ram.getEndAddress}%08x")
  println(f"ALL: ${addrSpace.getStartAddress}%08x => ${addrSpace.getEndAddress}%08x")

  val cpu = new MC68000();
  cpu.setAddressSpace(addrSpace);
  cpu.reset();
  new Monitor(cpu, addrSpace).run();
}
