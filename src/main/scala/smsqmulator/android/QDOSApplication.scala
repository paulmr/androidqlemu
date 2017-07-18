
package smsqmulator.android

import android.app.Application
import android.util.Log

class QDOSApplication extends Application {
  private val TAG = "QLApp"

  lazy val qdosMonitor = {
    val m = new qdos.QDOSMonitor(romFile = getResources.openRawResource(TR.raw.rom.resid))
    Log.d(TAG, "Writing rubbish to memory")
    for(addr <- (0x20000 until 0x28000) by 2) m.cpu.writeMemoryWord(addr, Integer.parseInt("1000100000100010", 2))
    Log.d(TAG, "success")
    m
  }
}
