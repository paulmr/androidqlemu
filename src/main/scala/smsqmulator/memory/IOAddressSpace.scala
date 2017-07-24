package m68k.memory

/**
  *  This is a special Address Space that is used (in the real
  *  machine) to communicate with peripherals. Here we are triggering
  *  special behaviour
  */

import IOAddressSpace._
import smsqmulator.util.Logger.log

class IOAddressSpace(val getStartAddress: Int, val getEndAddress: Int) extends AddressSpace {
  private var PC_INTR = 0

  def setInterrupt(i: Int) = PC_INTR |= i
  def clearInterrupt = PC_INTR = 0

  def size = getEndAddress - getStartAddress

  def isValid(addr: Int) = (addr >= getStartAddress) && (addr <= getEndAddress)

  def handleRead(addr: Int, size: Int): Int = {
    //println(f"IOAddressSpace read [$size]: $addr%08x")
    addr match {
      case REG_PC_INTR =>
        PC_INTR
      case REG_IPC_READ =>
        1
      case _ =>
        0
    }
  }

  def handleWrite(addr: Int, size: Int, value: Int): Unit = {
    println(f"IOAddressSpace write [$size]: $addr%08x => $value%08x/$value")
    addr match {
      case _ =>
    }
  }

  def internalReadByte(addr: Int): Int =
    handleRead(addr, 1)

  def internalReadLong(addr: Int) =
    handleRead(addr, 4)

  def internalReadWord(addr: Int) =
    handleRead(addr, 2)

  def internalWriteByte(addr: Int, value: Int) =
    handleWrite(addr, 1, value)

  def internalWriteWord(addr: Int, value: Int) =
    handleWrite(addr, 2, value)

  def internalWriteLong(addr: Int, value: Int) =
    handleWrite(addr, 4, value)

  def readByte(addr: Int) = internalReadByte(addr)
  def readLong(addr: Int) = internalReadLong(addr)
  def readWord(addr: Int) = internalReadWord(addr)

  def writeByte(addr: Int, value: Int) = internalWriteByte(addr, value)
  def writeWord(addr: Int, value: Int) = internalWriteWord(addr, value)
  def writeLong(addr: Int, value: Int) = internalWriteLong(addr, value)

  def reset = ()

}


object IOAddressSpace {
  val REG_IPC_WRITE  = 0x18003
  val REG_IPC_READ   = 0x18020
  val REG_PC_INTR    = 0x18021


  val INT_FINT = 2
}
