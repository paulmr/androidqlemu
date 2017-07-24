package smsqmulator.android

import android.util.Log
import android.content.Context
import android.util.AttributeSet
import android.view.{ SurfaceView, SurfaceHolder }

import android.graphics.{ Paint, Canvas, Bitmap, Color }

import smsqmulator.util.Ticker

import qdos.QLScreen

class ScreenView(context: Context, attrs: AttributeSet) extends SurfaceView(context, attrs)
    with QLScreen {

  type ColorT = Int

  val YELLOW = Color.YELLOW
  val BLACK = Color.BLACK
  val BLUE =  Color.BLUE
  val RED = Color.RED
  val MAGENTA  = Color.MAGENTA
  val GREEN = Color.GREEN
  val CYAN = Color.CYAN
  val WHITE = Color.WHITE

  val mon = context.getApplicationContext.asInstanceOf[QDOSApplication].qdosMonitor
  val cpu = mon.cpu

  private val TAG = "QLScreenView"

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

  protected def updateBitmap = {
    bitmap.setPixels(doQLScreen, 0, gWidth, 0, 0, gWidth, gHeight)
    // val endTime = System.nanoTime
  }

  protected def updateScreen = withCanvas { c =>
    updateBitmap
    c.drawBitmap(Bitmap.createScaledBitmap(bitmap, c.getWidth, c.getHeight, false), getMatrix, null)
  }
}
