package smsqmulator.android

import android.util.Log
import android.content.Context
import android.util.AttributeSet
import android.view.{ SurfaceView, SurfaceHolder }

import android.graphics.{ Paint, Canvas, Bitmap, Color }

import smsqmulator.util.Ticker

class ScreenView(context: Context, attrs: AttributeSet) extends SurfaceView(context, attrs) {

  private val mon = context.getApplicationContext.asInstanceOf[QDOSApplication].qdosMonitor
  private val cpu = mon.cpu

  private val TAG = "QLScreenView"

  // mode 4
  private val gWidth = 256
  private val gHeight = 256

  private var pixels = new Array[Int](gWidth * gHeight)

  val ticker = Ticker.fiftyHz(updateScreen _)
  ticker.start

  // this may need to be modified if the screen mode changes etc, although I think we might be able to call Config
  private lazy val bitmap = Bitmap.createBitmap(gWidth, gHeight, Bitmap.Config.RGB_565)

  val holder = getHolder

  // when our surface goes away, we can't do any drawing so there is
  // no point in being triggered by the ticker. It will continue to
  // 'tick' but it won't tell us about it until we unpause it.

  holder addCallback (new SurfaceHolder.Callback {
    def surfaceCreated(holder: SurfaceHolder) = { ticker.unpause }
    def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = { }
    def surfaceDestroyed(holder: SurfaceHolder) = { ticker.pause }
  })

  protected def withCanvas(f: Canvas => Unit) = {
    val canvas = holder.lockCanvas(null)
    if(canvas != null) {
      try {
        f(canvas)
      } catch {
        case e: Exception => Log.e(TAG, e.toString)
      } finally {
        holder.unlockCanvasAndPost(canvas)
      }
    }
  }

  val pixelmask = Vector(0x203,0x80c,0x2030,0x80c0)

  protected def updateBitmap = {
    // val startTime = System.nanoTime
    var addr = 0x20000
    var pixelNum = 0

    // this logic is nabbed from smsqmulator (thanks!)
    while(addr < 0x28000) {
      val data = mon.ram.readWord(addr)
      var i = 0
      while(i < 4) {
        val colourData = (data & pixelmask(i))>>>(i*2)
        val colour = colourData match {
          case 0 =>
            Color.BLACK
          case 1 =>
            Color.BLUE
          case 2 =>
            Color.RED
          case 3 =>
            Color.MAGENTA
          case 0x200 =>
            Color.GREEN
          case 0x201 =>
            Color.CYAN
          case 0x202 =>
            Color.YELLOW
          case 0x203 =>
            Color.WHITE
          case _ =>
            Log.e(TAG, f"Unkown colour: $colourData%x (defaulting)")
            Color.WHITE // ? was ORANGE
        }
        pixels(pixelNum + (3 - i)) = colour
        i += 1
      }

      pixelNum += 4
      addr += 2
    }
    bitmap.setPixels(pixels, 0, gWidth, 0, 0, gWidth, gHeight)
    // val endTime = System.nanoTime
  }

  protected def updateScreen = withCanvas { c =>
    updateBitmap
    c.drawBitmap(Bitmap.createScaledBitmap(bitmap, c.getWidth, c.getHeight, false), getMatrix, null)
  }
}
