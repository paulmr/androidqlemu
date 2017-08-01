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

    val (romFile, promFile) =
      if(args.length == 0) {
        ("rom/js.rom", None)
      } else if(args.length == 1) {
        (args(0), None)
      } else {
        (args(0), Some(args(1)))
      }

    val q = new QDOSMonitor(
      romFile = new FileInputStream(romFile),
      promFile = promFile.map(in => new FileInputStream(in))
    )

    new m68k.Monitor(q.cpu, q.addrSpace).run()

  }

  //   val cons = org.jline.reader.LineReaderBuilder.builder().build()

  //   @annotation.tailrec
  //   def cli(): Unit = {
  //     val s = try {
  //       cons.readLine("mon> ")
  //     } catch {
  //       case _: org.jline.reader.EndOfFileException => "quit"
  //     }

  //     if(s != "quit") {
  //       q.doCommand(s)
  //       cli()
  //     }
  //   }

  //   cli()
  //   q.shutdown()
  // }
}
