package smsqmulator.android

import android.view.{ Menu, MenuItem }

import smsqmulator.util.Logger.log

trait QLActionBar extends android.app.Activity with TypedFindView {
  def mon: qdos.QDOSMonitor

  def swapScreen(): Unit

  def toggleRunState(): Boolean =
    mon.toggleRunState() // returns new state

  lazy val runningIcon = TR.drawable.play_icon
  lazy val pausedIcon = TR.drawable.pause_icon

  def updateRunStateButton() = ()

  override def onCreateOptionsMenu(menu: Menu) = {
    getMenuInflater.inflate(TR.menu.action.resid, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case R.id.showScreen =>
        swapScreen()
        true
      case R.id.runState =>
        toggleRunState()
        updateRunStateButton()
        true
      case _ => false
    }
  }
}
