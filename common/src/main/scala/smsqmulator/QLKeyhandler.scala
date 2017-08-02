package qdos

trait QLKeyhandler {
  def mon: QDOSMonitor

  type KeyT

  def keymap: PartialFunction[KeyT, Int]

  // returns false if it can't handle this key
  def doKey(key: KeyT): Boolean =
    if(keymap.isDefinedAt(key)) {
        mon.addKey(keymap(key))
        true
    } else false
}

object QLKeyhandler {
  val KEY_F2 = 0x3b
  val KEY_A  = 0x1c
}
