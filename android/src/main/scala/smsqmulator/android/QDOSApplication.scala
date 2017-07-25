package smsqmulator.android

import android.app.Application
import android.util.Log

import java.io.{ File, FileInputStream, InputStream, IOException }

import smsqmulator.util.Logger

class QDOSApplication extends Application {
  private val TAG = "QLApp"

  Logger.setCb { s =>
    Log.d(TAG, s)
  }

  // lazy val promFile: Option[InputStream] = {
  //   try {
  //     val fname = new File(getExternalFilesDir(null), "qlemu.rom")
  //     val res = Some(new FileInputStream(fname))
  //     Logger.log(s"using local rom ${fname.toString}")
  //     res
  //   } catch {
  //     case e: IOException =>
  //       Logger.log(s"local qlemu.rom not found, using packaged (${e.toString})")
  //       Some(getResources.openRawResource(TR.raw.qlemurom.resid))
  //   }
  // }

  lazy val qdosMonitor =
    new qdos.QDOSMonitor(romFile = getResources.openRawResource(TR.raw.rom.resid))
}
