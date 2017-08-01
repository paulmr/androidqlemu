package qdos.io

/**
  *  This is a special Address Space that is used (in the real
  *  machine) to communicate with peripherals. Here we are triggering
  *  special behaviour
  */

import m68k.memory.AddressSpace

import smsqmulator.util.Logger.log


object IOSize extends Enumeration {
  val BYTE, LONG, WORD = Value
}

class IOAddressSpace(
  val getStartAddress: Int,
  val getEndAddress: Int,
  mon: qdos.QDOSMonitor
) extends AddressSpace {

  private var PC_INTR = 0

  def size = getEndAddress - getStartAddress

  def isValid(addr: Int) = (addr >= getStartAddress) && (addr <= getEndAddress)

  def handleRead(addr: Int, size: IOSize.Value): Int = addr match {
    case 0x18021 => PC_INTR
    case _ => 0
  }

  def handleWrite(addr: Int, value: Int, size: IOSize.Value): Unit = addr match {
    case 0x18021 =>
      PC_INTR = (value & 0xFF)
      log(f"[pc=${mon.cpu.getPC()}%08x] PC_INTR/$size set to: ${value.toBinaryString}")
    case _ => ()
  }

  def internalReadByte(addr: Int): Int =
    handleRead(addr, IOSize.BYTE)

  def internalReadLong(addr: Int) =
    handleRead(addr, IOSize.LONG)

  def internalReadWord(addr: Int) =
    handleRead(addr, IOSize.WORD)

  def internalWriteByte(addr: Int, value: Int) =
    handleWrite(addr, value, IOSize.BYTE)

  def internalWriteWord(addr: Int, value: Int) =
    handleWrite(addr, value, IOSize.WORD)

  def internalWriteLong(addr: Int, value: Int) =
    handleWrite(addr, value, IOSize.LONG)

  def readByte(addr: Int) = internalReadByte(addr)
  def readLong(addr: Int) = internalReadLong(addr)
  def readWord(addr: Int) = internalReadWord(addr)

  def writeByte(addr: Int, value: Int) = internalWriteByte(addr, value)
  def writeWord(addr: Int, value: Int) = internalWriteWord(addr, value)
  def writeLong(addr: Int, value: Int) = internalWriteLong(addr, value)

  def reset = ()

}
