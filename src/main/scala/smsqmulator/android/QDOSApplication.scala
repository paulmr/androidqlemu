package smsqmulator.android

import android.app.Application

class QDOSApplication extends Application {
  lazy val qdosMonitor = new qdos.QDOSMonitor(romFile = getResources.openRawResource(TR.raw.rom.resid))
}
