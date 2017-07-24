package qdos

trait QLKeyhandler {
  def mon: QDOSMonitor

  type KeyT

  def keymap: PartialFunction[KeyT, Int]

  // returns false if it can't handle this key
  def doKey(key: KeyT): Boolean =
    if(keymap.isDefinedAt(key)) {
        mon.enqueue(mon.sysVar_CUR_KEY_QUEUE, keymap(key))
        true
    } else false
}

object QLKeyhandler {
  val KEY_F2 = 236
  val KEY_A  =  97
}
