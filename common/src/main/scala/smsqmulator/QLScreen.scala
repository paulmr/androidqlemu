package qdos

trait QLScreen {

  type ColorT

  def mon: QDOSMonitor

  /* set these to appropriate values for whatever your system
   * needs. The funcs that process the screen memory will convert the
   * screen mem of QL colors to an array of these */

  val YELLOW  : ColorT
  val BLACK   : ColorT
  val BLUE    : ColorT
  val RED     : ColorT
  val MAGENTA : ColorT
  val GREEN   : ColorT
  val CYAN    : ColorT
  val WHITE   : ColorT

  // mode 4
  val gWidth = 256
  val gHeight = 256

  val SCREEN_BASE = 0x20000
  val SCREEN_END = 0x28000

  val pixelmask = Vector(0x203,0x80c,0x2030,0x80c0)

  def doQLScreen(implicit ct: scala.reflect.ClassTag[ColorT]) = {
    // val startTime = System.nanoTime
    var addr = SCREEN_BASE
    var pixelNum = 0
    val pixels = new Array[ColorT](gWidth * gHeight)
    // this logic is nabbed from smsqmulator (thanks!)
    while(addr < SCREEN_END) {
      val data = mon.ram.readWord(addr)
      var i = 0
      while(i < 4) {
        val colourData = (data & pixelmask(i))>>>(i*2)
        val colour = colourData match {
          case 0 =>     BLACK
          case 1 =>     BLUE
          case 2 =>     RED
          case 3 =>     MAGENTA
          case 0x200 => GREEN
          case 0x201 => CYAN
          case 0x202 => YELLOW
          case 0x203 => WHITE
          case _ =>
            WHITE // ? was ORANGE
        }
        pixels(pixelNum + (3 - i)) = colour
        i += 1
      }
      pixelNum += 4
      addr += 2
    }
    pixels
  }
}
