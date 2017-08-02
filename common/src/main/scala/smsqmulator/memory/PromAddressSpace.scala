package qdos

/**
  * This pretends to be a PROM (e.g. loaded at $C000) and is used to
  * trigger special behaviour to make the emulator work.
  */

import smsqmulator.util.Logger.log

import m68k.memory.ByteBufferAddressSpace
import java.nio.ByteBuffer

class PromAddressSpace(mon: QDOSMonitor, initStartAddr: Int, promName: String, promSize: Int = 16 * 1024)
    extends ByteBufferAddressSpace {

  private val bytes: Array[Byte] = Array.fill(promSize)(0.toByte)

  private val INS_RTS = 0x4e75 // assembled RTS instruction

  this.size = bytes.size
  this.buffer = ByteBuffer.wrap(bytes)

  this.startAddr = initStartAddr

  private val initOffset = 0xBE // init func (magic addr)
  private val initAddr   = initStartAddr + 0xBE // relative to start of prom

  def writeString(addr: Int, s: String) = {
    val bytes = s.getBytes
    bytes.foldLeft(addr) { (addr, byte) => writeByte(addr, byte); addr + 1 }
  }

  def init() = {
    val addr = startAddr
    writeLong(addr, 0x4AFB0001) // magic prom identifier
    writeWord(addr + 4, 0)      // no sbasic funcs
    writeWord(addr + 6, initOffset)

    // prom name
    writeWord(addr + 8, promName.length)
    writeString(addr + 10, promName)

    this
  }

  def doCustomInit() = {
    log("Custom prom init triggered")
    log("disabling MDV driver")
    mon.setSysVar(QDOSMonitor.SV.DDLST, 0)
  }

  override def readWord(addr: Int) = {
    addr match {
      case `initAddr` =>
        doCustomInit() // trigger init
        INS_RTS
      case _ => super.readWord(addr)
    }
  }

}
