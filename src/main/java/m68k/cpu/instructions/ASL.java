package m68k.cpu.instructions;

import m68k.cpu.*;
/*
//  M68k - Java Amiga MachineCore
//  Copyright (c) 2008-2010, Tony Headford
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
//  following conditions are met:
//
//    o  Redistributions of source code must retain the above copyright notice, this list of conditions and the
//       following disclaimer.
//    o  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
//       following disclaimer in the documentation and/or other materials provided with the distribution.
//    o  Neither the name of the M68k Project nor the names of its contributors may be used to endorse or promote
//       products derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
//  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
*/
public class ASL implements InstructionHandler
{
	protected final Cpu cpu;

	public ASL(Cpu cpu)
	{
		this.cpu = cpu;
	}

	public void register(InstructionSet is)
	{
		int base;
		Instruction i;

		for(int sz = 0; sz < 3; sz++)
		{
			if(sz == 0)
			{
				// asl byte imm
				base = 0xe100;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return asl_byte_imm(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, Size.Byte);
					}
				};
			}
			else if(sz == 1)
			{
				// asl word imm
				base = 0xe140;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return asl_word_imm(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, Size.Word);
					}
				};
			}
			else
			{
				// asl long imm
				base = 0xe180;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return asl_long_imm(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, Size.Long);
					}
				};
			}
			for(int imm = 0; imm < 8; imm++)
			{
				for(int reg = 0; reg < 8; reg++)
				{
					is.addInstruction(base + (imm << 9) + reg, i);
				}
			}
		}

		for(int sz = 0; sz < 3; sz++)
		{
			if(sz == 0)
			{
				// asl byte reg
				base = 0xe120;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return asl_byte_reg(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, Size.Byte);
					}
				};
			}
			else if(sz == 1)
			{
				// asl word reg
				base = 0xe160;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return asl_word_reg(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, Size.Word);
					}
				};
			}
			else
			{
				// asl long reg
				base = 0xe1a0;
				i = new Instruction() {
					public int execute(int opcode)
					{
						return asl_long_reg(opcode);
					}
					public DisassembledInstruction disassemble(int address, int opcode)
					{
						return disassembleOp(address, opcode, Size.Long);
					}
				};
			}
			for(int imm = 0; imm < 8; imm++)
			{
				for(int reg = 0; reg < 8; reg++)
				{
					is.addInstruction(base + (imm << 9) + reg, i);
				}
			}
		}

		// asl word mem
		base = 0xe1c0;
		i = new Instruction() {
			public int execute(int opcode)
			{
				return asl_word_mem(opcode);
			}
			public DisassembledInstruction disassemble(int address, int opcode)
			{
				return disassembleOp(address, opcode, Size.Word);
			}
		};

		for(int ea_mode = 2; ea_mode < 8; ea_mode++)
		{
			for(int ea_reg = 0; ea_reg < 8; ea_reg++)
			{
				if(ea_mode == 7 && ea_reg > 1)
					break;
				is.addInstruction(base + (ea_mode << 3) + ea_reg, i);
			}
		}
	}

	protected int asl_byte_imm(int opcode)
	{
		int shift = (opcode >> 9) & 0x07;
		if(shift == 0)
			shift = 8;

		int reg = (opcode & 0x07);
		int d = cpu.getDataRegisterByte(reg);

		int msb;
		int last_out = 0;
		int msb_changed = 0;
		for(int s= 0; s < shift; s++)
		{
			last_out = d & 0x80;
			d <<= 1;
			msb = d & 0x80;
			if(msb != last_out)
				msb_changed = 1;
		}
		d &= 0x00ff;
		cpu.setDataRegisterByte(reg, d);

		cpu.calcFlagsParam(InstructionType.ASL, shift, last_out, d, msb_changed, Size.Byte);
		return 6 + shift + shift;
	}

	protected int asl_word_imm(int opcode)
	{
		int shift = (opcode >> 9) & 0x07;
		if(shift == 0)
			shift = 8;

		int reg = (opcode & 0x07);
		int d = cpu.getDataRegisterWord(reg);

		int msb;
		int last_out = 0;
		int msb_changed = 0;
		for(int s= 0; s < shift; s++)
		{
			last_out = d & 0x8000;
			d <<= 1;
			msb = d & 0x8000;
			if(msb != last_out)
				msb_changed = 1;
		}
		d &= 0x0000ffff;
		cpu.setDataRegisterWord(reg, d);

		cpu.calcFlagsParam(InstructionType.ASL, shift, last_out, d, msb_changed, Size.Word);
		return 6 + shift + shift;
	}

	protected int asl_long_imm(int opcode)
	{
		int shift = (opcode >> 9) & 0x07;
		if(shift == 0)
			shift = 8;

		int reg = (opcode & 0x07);
		int d = cpu.getDataRegisterLong(reg);

		int msb;
		int last_out = 0;
		int msb_changed = 0;
		for(int s= 0; s < shift; s++)
		{
			last_out = d & 0x80000000;
			d <<= 1;
			msb = d & 0x80000000;
			if(msb != last_out)
				msb_changed = 1;
		}
		cpu.setDataRegisterLong(reg, d);

		cpu.calcFlagsParam(InstructionType.ASL, shift, last_out, d, msb_changed, Size.Long);
		return 8 + shift + shift;
	}

	protected int asl_byte_reg(int opcode)
	{
            // wl this is mod 64 not 32
		int shift = cpu.getDataRegisterLong((opcode >> 9) & 0x07) & 63;

		int reg = (opcode & 0x07);
		int d = cpu.getDataRegisterByte(reg);

		int msb;
		int last_out = 0;
		int msb_changed = 0;
		for(int s= 0; s < shift; s++)
		{
			last_out = d & 0x80;
			d <<= 1;
			msb = d & 0x80;
			if(msb != last_out)
				msb_changed = 1;
		}
		d &= 0x00ff;
		cpu.setDataRegisterByte(reg, d);

		cpu.calcFlagsParam(InstructionType.ASL, shift, last_out, d, msb_changed, Size.Byte);
		return 6 + shift + shift;
	}

	protected int asl_word_reg(int opcode)
	{
            // wl this is mod 64 not 32
		int shift = cpu.getDataRegisterLong((opcode >> 9) & 0x07) & 63;

		int reg = (opcode & 0x07);
		int d = cpu.getDataRegisterWord(reg);

		int msb;
		int last_out = 0;
		int msb_changed = 0;
		for(int s= 0; s < shift; s++)
		{
			last_out = d & 0x8000;
			d <<= 1;
			msb = d & 0x8000;
			if(msb != last_out)
				msb_changed = 1;
		}
		d &= 0x0000ffff;
		cpu.setDataRegisterWord(reg, d);

		cpu.calcFlagsParam(InstructionType.ASL, shift, last_out, d, msb_changed, Size.Byte);
		return 6 + shift + shift;
	}

	protected int asl_long_reg(int opcode)
	{
            // wl this is mod 64 not 32
		int shift = cpu.getDataRegisterLong((opcode >> 9) & 0x07) & 63;

		int reg = (opcode & 0x07);
		int d = cpu.getDataRegisterLong(reg);

		int msb;
		int last_out = 0;
		int msb_changed = 0;
		for(int s= 0; s < shift; s++)
		{
			last_out = d & 0x80000000;
			d <<= 1;
			msb = d & 0x80000000;
			if(msb != last_out)
				msb_changed = 1;
		}
		cpu.setDataRegisterLong(reg, d);

		cpu.calcFlagsParam(InstructionType.ASL, shift, last_out, d, msb_changed, Size.Long);
		return 8 + shift + shift;
	}

	protected int asl_word_mem(int opcode)
	{
		Operand op = cpu.resolveDstEA((opcode >> 3) & 0x07, (opcode & 0x07),Size.Word);
		int v = op.getWord();
		int last_out = v & 0x8000;
		int msb_changed = 0;

		v <<= 1;
		if((v & 0x8000) != last_out)
			msb_changed = 1;

		op.setWord(v);
		cpu.calcFlagsParam(InstructionType.ASL, 1, last_out, v, msb_changed, Size.Word);
		return 8 + op.getTiming();
	}

	protected final DisassembledInstruction disassembleOp(int address, int opcode, Size sz)
	{
		DisassembledOperand src;
		DisassembledOperand dst;

		if((opcode & 0x00c0) == 0x00c0)
		{
			//mem mode
			src = cpu.disassembleDstEA(address + 2, (opcode >> 3) & 0x07, (opcode & 0x07), sz);
			return new DisassembledInstruction(address, opcode, "asl" + sz.ext(), src);
		}
		else if((opcode & 0x0020) == 0x0020)
		{
			//reg mode
			src = new DisassembledOperand("d" + ((opcode >> 9) & 0x07));
			dst = new DisassembledOperand("d" + (opcode & 0x07));
		}
		else
		{
			//immediate mode
			int count = (opcode >> 9) & 0x07;
			if(count == 0)
				count = 8;

			src = new DisassembledOperand("#" + count);
			dst = new DisassembledOperand("d" + (opcode & 0x07));
		}

		return new DisassembledInstruction(address, opcode, "asl" + sz.ext(), src, dst);
	}
	
}
