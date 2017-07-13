package smsqmulator.android

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.graphics.drawable.Animatable

class MonitorActivity extends AppCompatActivity with TypedFindView {
  lazy val regText = findView(TR.regText)

  // allows accessing `.value` on TR.resource.constants
  implicit val context = this

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    // type ascription is required due to SCL-10491
    val monitorView = TypedViewHolder.setContentView(this, TR.layout.monitor)
    updateRegisters
  }

  def updateRegisters = {
    regText.setText("Registers")
  }

}
