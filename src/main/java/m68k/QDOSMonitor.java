package qdos;

import m68k.cpu.Cpu;
import m68k.cpu.MC68000;
import m68k.Monitor;

import java.io.IOException;
import java.io.FileInputStream;
import m68k.memory.ROMAddressSpace;

public class QDOSMonitor {
    static final int ramSize = 0x100000;

    public static void main(String[] args) {
        String fname = args[0];
        try {
            FileInputStream fin = new FileInputStream(fname);
            ROMAddressSpace rom = new ROMAddressSpace(fin.getChannel(), ramSize);
            Cpu cpu = new MC68000();
            cpu.setAddressSpace(rom);
            cpu.reset();
            cpu.setPC(ramSize);
            new Monitor(cpu, rom).run();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}
