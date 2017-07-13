package m68k.memory

import java.io.InputStream
import scala.collection.mutable.ArrayBuffer
import java.nio.ByteBuffer

class InputStreamAddressSpace(in: InputStream, initStartAddr: Int)
    extends ByteBufferAddressSpace {

  private val bytes: Array[Byte] = {
    val buf = new ArrayBuffer[Byte]
    var input: Int = in.read
    while(input != -1) {
      buf += input.toByte
      input = in.read
    }
    buf.toArray
  }

  this.buffer = ByteBuffer.wrap(bytes)
  this.size = bytes.size

  this.startAddr = initStartAddr

  // def getStartAddress = startAddr
  // def getStartAddress = low.getStartAddress

  // def size = low.size + high.size

  // protected def forAddr(addr: Int): AddressSpace = {
  //   if(addr < low.getStartAddress || (addr > low.getEndAddress && addr < high.getStartAddress))
  //     throw new Exception(f"Unmapped memory address 0x$addr%04X")

  //   if(addr < (low.getEndAddress)) low else high
  // }

  // def internalReadByte(addr: Int) = forAddr(addr).readByte(addr)
  // def internalReadLong(addr: Int) = forAddr(addr).readLong(addr)
  // def internalReadWord(addr: Int) = forAddr(addr).readWord(addr)
  // def internalWriteByte(addr: Int, value: Int) = forAddr(addr).writeByte(addr, value)
  // def internalWriteWord(addr: Int, value: Int) = forAddr(addr).writeWord(addr, value)
  // def internalWriteLong(addr: Int, value: Int) = forAddr(addr).writeLong(addr, value)

  // def readByte(addr: Int) = internalReadByte(addr)
  // def readLong(addr: Int) = internalReadLong(addr)
  // def readWord(addr: Int) = internalReadWord(addr)

  // def writeByte(addr: Int, value: Int) = internalWriteByte(addr, value)
  // def writeWord(addr: Int, value: Int) = internalWriteWord(addr, value)
  // def writeLong(addr: Int, value: Int) = internalWriteLong(addr, value)

  // def reset = {
  //   low.reset
  //   high.reset
  // }

}
