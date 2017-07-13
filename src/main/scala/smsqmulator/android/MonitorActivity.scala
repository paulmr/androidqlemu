package smsqmulator.android

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.graphics.drawable.Animatable

class MonitorActivity extends AppCompatActivity with TypedFindView {
  // allows accessing `.value` on TR.resource.constants
  implicit val context = this

  lazy val regText = findView(TR.regText)
  lazy val memText = findView(TR.memText)

  lazy val mon = {
    val x = context.getResources.openRawResource(TR.raw.rom.resid)
    val f = context.getResources.openRawResourceFd(TR.raw.rom.resid)
    new qdos.QDOSMonitor(romFile = f.createInputStream)
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    // type ascription is required due to SCL-10491
    val monitorView = TypedViewHolder.setContentView(this, TR.layout.monitor)
    updateRegisters
  }

  def writeError(s: String) = memText.setText(s)

  def updateRegisters = {
    try {
      val pc = mon.cpu.getPC()
      regText.setText(f"Registers\nPC = ${pc}%04X")
    } catch {
      case e: Exception => writeError(e.toString + "\n" + e.getStackTrace().map(_.toString).mkString("\n"))
    }

  }

}
