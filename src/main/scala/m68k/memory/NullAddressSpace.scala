package m68k.memory

class NullAddressSpace(startAddress: Int) extends AddressSpace {

  def getEndAddress = startAddress
  def getStartAddress = startAddress

  def size = 0

  def internalReadByte(addr: Int) = 0
  def internalReadLong(addr: Int) = 0
  def internalReadWord(addr: Int) = 0
  def internalWriteByte(addr: Int, value: Int) = ()
  def internalWriteWord(addr: Int, value: Int) = ()
  def internalWriteLong(addr: Int, value: Int) = ()

  def readByte(addr: Int) = internalReadByte(addr)
  def readLong(addr: Int) = internalReadLong(addr)
  def readWord(addr: Int) = internalReadWord(addr)

  def writeByte(addr: Int, value: Int) = internalWriteByte(addr, value)
  def writeWord(addr: Int, value: Int) = internalWriteWord(addr, value)
  def writeLong(addr: Int, value: Int) = internalWriteLong(addr, value)

  def reset = ()

}
