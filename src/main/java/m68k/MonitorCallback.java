package m68k;

import m68k.cpu.Cpu;

public interface MonitorCallback {
    public void step(Cpu cpu);
}
