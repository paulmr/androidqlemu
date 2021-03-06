package m68k.memory

class NullAddressSpace(startAddress: Int) extends AddressSpace {

  def getEndAddress = startAddress
  def getStartAddress = startAddress

  def size = 0

  def isValid(addr: Int) = false

  def internalReadByte(addr: Int) = throw new MemoryException(s"Invalid addr $addr")
  def internalReadLong(addr: Int) = throw new MemoryException(s"Invalid addr $addr")
  def internalReadWord(addr: Int) = throw new MemoryException(s"Invalid addr $addr")
  def internalWriteByte(addr: Int, value: Int) =
    throw new MemoryException(s"Invalid addr $addr")
  def internalWriteWord(addr: Int, value: Int) =
    throw new MemoryException(s"Invalid addr $addr")
  def internalWriteLong(addr: Int, value: Int) =
    throw new MemoryException(s"Invalid addr $addr")

  def readByte(addr: Int) = internalReadByte(addr)
  def readLong(addr: Int) = internalReadLong(addr)
  def readWord(addr: Int) = internalReadWord(addr)

  def writeByte(addr: Int, value: Int) = internalWriteByte(addr, value)
  def writeWord(addr: Int, value: Int) = internalWriteWord(addr, value)
  def writeLong(addr: Int, value: Int) = internalWriteLong(addr, value)

  def reset = ()

}
