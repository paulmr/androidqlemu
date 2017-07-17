package smsqmulator.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.graphics.Paint

class QLScreenActivity extends AppCompatActivity with TypedFindView {

  lazy val surfaceView = findView(TR.qlScreenView)
  lazy val surfaceHolder = surfaceView.getHolder

  implicit val context = this

  override def onCreate(savedInstance: Bundle): Unit = {
    super.onCreate(savedInstance)

    val monitorView = TypedViewHolder.setContentView(this,
      TR.layout.qlscreen)

    updateScreen
  }

  def updateScreen = {
    val x = surfaceHolder
    //val canvas = surfaceHolder.lockCanvas
    // val w = canvas.getWidth
    // val h = canvas.getHeight

    // canvas.drawCircle(w / 2, h / 2, w / 2, new Paint())
    //surfaceHolder.unlockCanvasAndPost(canvas)
  }

}
