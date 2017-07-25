package qdos

import java.io.FileInputStream

import smsqmulator.util.Logger

object QDOSMonitorMain {
  def parseInt(value: String) =
    if(value.startsWith("$"))
      (java.lang.Long.parseLong(value.substring(1), 16) & 0x0ffffffffL).toInt
    else
      (java.lang.Long.parseLong(value) & 0x0ffffffffL).toInt

  def main(args: Array[String]) = {

    Logger.setCb(println _)

    val q = new QDOSMonitor(
      romFile = new FileInputStream(args.headOption.getOrElse("rom/js.rom"))
    )

    val cons = org.jline.reader.LineReaderBuilder.builder().build()

    @annotation.tailrec
    def cli(): Unit = {
      val s = try {
        cons.readLine("mon> ")
      } catch {
        case _: org.jline.reader.EndOfFileException => "quit"
      }

      if(s != "quit") {
        q.doCommand(s)
        cli()
      }
    }

    cli()
    q.shutdown()
  }
}
