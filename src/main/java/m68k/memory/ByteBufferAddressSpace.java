package m68k.memory;

import java.nio.ByteBuffer;

public class ByteBufferAddressSpace implements AddressSpace {
	protected ByteBuffer buffer;
	protected int size;
        protected int startAddr = 0;

	public void reset()
	{
	}

	public int getStartAddress()
	{
		return startAddr;
	}

	public int getEndAddress()
	{
		return startAddr + (size - 1);
	}

	public int readByte(int addr)
	{
                int addrToGet = addr - startAddr;
		int v = buffer.get(addrToGet);
		return v & 0x00ff;
	}

	public int readWord(int addr)
	{
                int addrToGet = addr - startAddr;
		int v =  buffer.getShort(addrToGet);
		return v & 0x0000ffff;
	}

	public int readLong(int addr)
	{
                int addrToGet = addr - startAddr;
		return buffer.getInt(addrToGet);
	}

	public void writeByte(int addr, int value)
	{
		buffer.put(addr - startAddr, (byte)(value & 0x00ff));
	}

	public void writeWord(int addr, int value)
	{
		buffer.putShort(addr - startAddr, (short)(value & 0x0000ffff));
	}

	public void writeLong(int addr, int value)
	{
		buffer.putInt(addr - startAddr, value);
	}

	public int internalReadByte(int addr)
	{
		return readByte(addr);
	}

	public int internalReadWord(int addr)
	{
		return readWord(addr);
	}

	public int internalReadLong(int addr)
	{
		return readLong(addr);
	}

	public void internalWriteByte(int addr, int value)
	{
		writeByte(addr, value);
	}

	public void internalWriteWord(int addr, int value)
	{
		writeWord(addr, value);
	}

	public void internalWriteLong(int addr, int value)
	{
		writeLong(addr, value);
	}

	public int size()
	{
		return size;
	}
}
