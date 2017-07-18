package smsqmulator.android

import android.util.Log
import android.content.Context
import android.util.AttributeSet
import android.view.{ SurfaceView, SurfaceHolder }

import android.graphics.{ Paint, Canvas, Bitmap, Color }

object QLColours {
  val blueMask = 1
  val redMask  = blueMask << 1
  val flashMask = blueMask << 8
  val greenMask = flashMask << 1

  def memToPixel(data: Int, num: Int): (Int, Int, Int) = {
    (if((data & (redMask << (num * 2))) == 0) 0 else 255,
      if((data & (greenMask << (num * 2))) == 0) 0 else 255,
      if((data & (blueMask << (num * 2))) == 0) 0 else 255)
  }

  def memToPixels(data: Int): Seq[Int] = (0 to 3).reverse map { n =>
    val (r, g, b) = memToPixel(data, n)
    Color.rgb(r, g, b)
  }
}

class ScreenView(context: Context, attrs: AttributeSet) extends SurfaceView(context, attrs) {

  private val cpu = context.getApplicationContext.asInstanceOf[QDOSApplication].qdosMonitor.cpu

  private val TAG = "QLScreenView"

  // this may need to be modified if the screen mode changes etc, although I think we might be able to call Config
  private lazy val bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.RGB_565)

  val holder = getHolder

  holder addCallback (new SurfaceHolder.Callback {
    def surfaceCreated(holder: SurfaceHolder) = {
      updateScreen
    }
    def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = {
      updateScreen
    }
    def surfaceDestroyed(holder: SurfaceHolder) = {
    }
  })

  protected def withCanvas(f: Canvas => Unit) = {
    val canvas = holder.lockCanvas(null)
    try {
      f(canvas)
    } catch {
      case e: Exception => Log.d(TAG, e.toString)
    } finally {
      holder.unlockCanvasAndPost(canvas)
    }
  }

  protected def updateBitmap = {
    val screenBase = 0x20000

    val pixels = ((0 until 0x8000) by 2) flatMap { count =>
      val addr = screenBase + count
      val data = cpu.readMemoryWord(addr)
      QLColours.memToPixels(data)
    }
    bitmap.setPixels(pixels.toArray, 0, 256, 0, 0, 256, 256)
  }

  protected def updateScreen = withCanvas { c =>
    updateBitmap
    c.drawBitmap(Bitmap.createScaledBitmap(bitmap, c.getWidth, c.getHeight, false), getMatrix, null)
    //c drawText ("hello world", 10, 10, QLColours.red)
  }

  def update = updateScreen
}
