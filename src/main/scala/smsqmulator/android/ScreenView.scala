package smsqmulator.android

import android.content.Context
import android.util.AttributeSet
import android.view.{ SurfaceView, SurfaceHolder }

import android.graphics.{ Paint, Canvas }

class ScreenView(context: Context, attrs: AttributeSet)
    extends SurfaceView(context, attrs) {

  val holder = getHolder

  holder addCallback (new SurfaceHolder.Callback {
    def surfaceCreated(holder: SurfaceHolder) = {
      val bluishSilver = new Paint
      bluishSilver.setARGB(255, 210, 255, 255)
      withCanvas { c =>
        c drawText ("hello world", 10, 10, bluishSilver)
      }
    }
    def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = {
    }
    def surfaceDestroyed(holder: SurfaceHolder) = {
    }
  })

  def withCanvas(f: Canvas => Unit) = {
    val canvas = holder.lockCanvas(null)
    try {
      f(canvas)
    } finally {
      holder.unlockCanvasAndPost(canvas)
    }
  }

}
