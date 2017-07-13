package m68k.memory

class LinkedAddressSpace(low: AddressSpace, high: AddressSpace) extends AddressSpace {

  require(low.getEndAddress < high.getStartAddress)

  def getEndAddress = high.getEndAddress
  def getStartAddress = low.getStartAddress

  def size = low.size + high.size

  protected def forAddr(addr: Int): AddressSpace = {
    if(addr < low.getStartAddress || (addr > low.getEndAddress && addr < high.getStartAddress))
      throw new MemoryException(f"Unmapped memory address 0x$addr%04X")

    if(addr < (low.getEndAddress)) low else high
  }

  def isValid(addr: Int) = low.isValid(addr) || high.isValid(addr)


  def internalReadByte(addr: Int) = forAddr(addr).readByte(addr)
  def internalReadLong(addr: Int) = forAddr(addr).readLong(addr)
  def internalReadWord(addr: Int) = forAddr(addr).readWord(addr)
  def internalWriteByte(addr: Int, value: Int) = forAddr(addr).writeByte(addr, value)
  def internalWriteWord(addr: Int, value: Int) = forAddr(addr).writeWord(addr, value)
  def internalWriteLong(addr: Int, value: Int) = forAddr(addr).writeLong(addr, value)

  def readByte(addr: Int) = internalReadByte(addr)
  def readLong(addr: Int) = internalReadLong(addr)
  def readWord(addr: Int) = internalReadWord(addr)

  def writeByte(addr: Int, value: Int) = internalWriteByte(addr, value)
  def writeWord(addr: Int, value: Int) = internalWriteWord(addr, value)
  def writeLong(addr: Int, value: Int) = internalWriteLong(addr, value)

  def reset = {
    low.reset
    high.reset
  }

}
