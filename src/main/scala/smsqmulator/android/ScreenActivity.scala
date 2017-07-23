package smsqmulator.android

import android.content.Intent

import android.content.res.Configuration
import android.view.{ View, Menu, MenuItem, KeyEvent }
import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class ScreenActivity extends AppCompatActivity with TypedFindView with QLActionBar {
  private val TAG = "QLScreenActivity"

  // allows accessing `.value` on TR.resource.constants
  implicit val context = this

  lazy val screen = findView(TR.qlScreenView)

  lazy val mon = context.getApplicationContext.asInstanceOf[QDOSApplication].qdosMonitor

  def swapScreen =
    startActivity(new Intent(this, classOf[MonitorActivity]))

  val keymap = Map(
    KeyEvent.KEYCODE_VOLUME_DOWN -> 236,
    KeyEvent.KEYCODE_A -> 97
  )

  override def onKeyDown(key: Int, keyev: KeyEvent) = {
    keymap.get(key) match {
      case Some(qlkey) =>
        mon.enqueue(mon.sysVar_CUR_KEY_QUEUE, qlkey)
        true
      case None => false
    }
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    // type ascription is required due to SCL-10491
    val screenView = TypedViewHolder.setContentView(this, TR.layout.qlscreen)
  }

  override def onConfigurationChanged(cfg: Configuration) = ()

}
