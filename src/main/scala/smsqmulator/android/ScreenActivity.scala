package smsqmulator.android

import android.content.res.Configuration
import android.view.{ View, Menu, MenuItem }
import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ScreenActivity extends AppCompatActivity with TypedFindView {
  // allows accessing `.value` on TR.resource.constants
  implicit val context = this

  lazy val screen = findView(TR.qlScreenView)

  lazy val mon = context.getApplicationContext.asInstanceOf[QDOSApplication].qdosMonitor

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    // type ascription is required due to SCL-10491
    val screenView = TypedViewHolder.setContentView(this, TR.layout.qlscreen)
  }

  override def onCreateOptionsMenu(menu: Menu) = {
    // getMenuInflater.inflate(TR.menu.action.resid, menu)
    // true
    false
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    false
  }

  override def onConfigurationChanged(cfg: Configuration) = ()
}
