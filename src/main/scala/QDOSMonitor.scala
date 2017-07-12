package qdos;

import m68k.memory._
import m68k.cpu.{ Cpu, MC68000 }
import m68k.Monitor;

import java.io.{ IOException, FileInputStream }

object QDOSMonitor extends App {
  val ramSize = 1024 // kilobytes
  
  val fin = new FileInputStream(args.headOption.getOrElse("SMSQE"))
  
  val rom = new ROMAddressSpace(fin.getChannel(), ramSize * 1024)
  val ram = new MemorySpace(1024)

  val addrSpace = new LinkedAddressSpace(ram, rom);

  println(f"RAM: ${ram.getStartAddress}%08x => ${ram.getEndAddress}%08x")
  println(f"ROM: ${rom.getStartAddress}%08x => ${rom.getEndAddress}%08x")

  val cpu = new MC68000();
  cpu.setAddressSpace(addrSpace);
  cpu.reset();
  cpu.setPC(rom.getStartAddress);
  new Monitor(cpu, addrSpace).run();
}
