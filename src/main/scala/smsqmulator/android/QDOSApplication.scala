
package smsqmulator.android

import android.app.Application
import android.util.Log

class QDOSApplication extends Application {
  private val TAG = "QLApp"

  lazy val qdosMonitor =
    new qdos.QDOSMonitor(romFile = getResources.openRawResource(TR.raw.rom.resid))
}
