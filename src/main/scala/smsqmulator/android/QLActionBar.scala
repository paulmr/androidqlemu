package smsqmulator.android

import android.view.{ Menu, MenuItem }

trait QLActionBar extends android.app.Activity {
  def swapScreen: Unit

  override def onCreateOptionsMenu(menu: Menu) = {
    getMenuInflater.inflate(TR.menu.action.resid, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case R.id.showScreen =>
        swapScreen
        true
      case _ => false
    }
  }
}
