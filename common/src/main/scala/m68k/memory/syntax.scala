package m68k.memory

object syntax {

  implicit class AddressSpaceSyntax(as: AddressSpace) {
    def ~>(other: AddressSpace) = new LinkedAddressSpace(as, other)
  }

}
