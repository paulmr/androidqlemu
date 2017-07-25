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

}
