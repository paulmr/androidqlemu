package qdos.io

/**
  *  This is a special Address Space that is used (in the real
  *  machine) to communicate with peripherals. Here we are triggering
  *  special behaviour
  */

import m68k.memory.AddressSpace

import IOAddressSpace._
import smsqmulator.util.Logger.log


object IOSize extends Enumeration {
  val BYTE, LONG, WORD = Value
}

trait IOControlRead {
  def read(addr: Int, size: IOSize.Value): Int
}

trait IOControlWrite {
  def write(addr: Int, value: Int, size: IOSize.Value): Unit
}

trait IOControl extends IOControlRead with IOControlWrite

trait IOWriteNull extends IOControlWrite {
  def write(addr: Int, value: Int, size: IOSize.Value): Unit = ()
}

trait IOReadNull extends IOControlRead {
  def read(addr: Int, size: IOSize.Value): Int = 0
}

trait IOTransmit extends IOControlWrite {

  private var mode = 0
  
  private val MODE_MASK = 0x18

  def write(addr: Int, value: Int, size: IOSize.Value): Unit = {
    val s = (value & 0xFF).toBinaryString
    mode = value & MODE_MASK
    log(f"Transmit reg: writing ${s}B; mode set to 0x$mode%2x")
  }
}

class IOAddressSpace(
  val getStartAddress: Int,
  val getEndAddress: Int,
  controlMap: Map[Int, IOControl] = Map.empty
) extends AddressSpace {

  private var PC_INTR = 0

  def setInterrupt(i: Int) = PC_INTR |= i
  def clearInterrupt = PC_INTR = 0

  def size = getEndAddress - getStartAddress

  def isValid(addr: Int) = (addr >= getStartAddress) && (addr <= getEndAddress)

  def handleRead(addr: Int, size: IOSize.Value): Int = {
    (controlMap get addr) match {
      case None => 0
      case Some(handler) => handler.read(addr, size)
    }
  }

  def handleWrite(addr: Int, value: Int, size: IOSize.Value): Unit = {
    (controlMap get addr) match {
      case None => ()
      case Some(handler) => handler.write(addr, value, size)
    }
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
