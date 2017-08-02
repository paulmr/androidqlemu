package qdos

import java.io.FileInputStream

import smsqmulator.util.Logger

import com.typesafe.config.ConfigFactory

object QDOSMonitorMain {

  import Logger.log

  lazy val config = ConfigFactory.load();

  def parseInt(value: String) =
    if(value.startsWith("$"))
      (java.lang.Long.parseLong(value.substring(1), 16) & 0x0ffffffffL).toInt
    else
      (java.lang.Long.parseLong(value) & 0x0ffffffffL).toInt

  def main(args: Array[String]) = {

    Logger.setCb(println _)

    val romFile = config.getString("qdos.romFile")
    val promFile = if(config.hasPath("qdos.promFile")) Some(config.getString("qdos.promFile")) else None

    val q = new QDOSMonitor(
      romFile = new FileInputStream(romFile),
      promFile = promFile.map(in => new FileInputStream(in))
    )
    new m68k.Monitor(q.cpu, q.addrSpace).run()
  }
}
