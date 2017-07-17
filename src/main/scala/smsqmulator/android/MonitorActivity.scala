package smsqmulator.android

import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import android.view.View
import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import java.io.{ PrintWriter, ByteArrayOutputStream }

import m68k.cpu.Cpu

class MonitorActivity extends AppCompatActivity with TypedFindView {
  // allows accessing `.value` on TR.resource.constants
  implicit val context = this
  lazy val cfg = context.getResources.getConfiguration

  lazy val regText = findView(TR.regText)
  lazy val memText = findView(TR.memText)
  lazy val cmdInput = findView(TR.commandInput)

  lazy val mon = context.getApplicationContext.asInstanceOf[QDOSApplication].qdosMonitor

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    // type ascription is required due to SCL-10491
    val monitorView = TypedViewHolder.setContentView(this, TR.layout.monitor)
    update
  }

  override def onConfigurationChanged(cfg: Configuration) = update

  def update = {
    updateRegisters
    updateMemoryDump
  }

  def writeError(s: String) = memText.setText(s)

  def hexStr(i: Int): String = f"${i}%08X"

  def makeCols(ncols: Int, s: Seq[String]): String =
    if(s.length < ncols) s.mkString(" ")
    else s.take(ncols).mkString(" ") + "\n" + makeCols(ncols, s.drop(ncols))

  def updateRegisters = {
    val ncols = if(isWide) 4 else 3
    val cpu = mon.cpu

    /* copied this from the Java Monitor Code */

    val flagView = {
      val sb = new StringBuilder(5);
      sb += (if(cpu.isFlagSet(Cpu.X_FLAG)) 'X' else '-')
      sb += (if(cpu.isFlagSet(Cpu.N_FLAG)) 'N' else '-')
      sb += (if(cpu.isFlagSet(Cpu.Z_FLAG)) 'Z' else '-')
      sb += (if(cpu.isFlagSet(Cpu.V_FLAG)) 'V' else '-')
      sb += (if(cpu.isFlagSet(Cpu.C_FLAG))  'C' else '-')
      sb.toString()
    }

    val dregs = (0 to 7)  map { reg =>
      f"D$reg : ${cpu.getDataRegisterLong(reg)}%08x"
    }

    val aregs = (0 to 7)  map { reg =>
      s"A$reg : ${hexStr(cpu.getAddrRegisterLong(reg))}"
    }

    val otherRegs = List(
      s"PC : ${hexStr(cpu.getPC)}",
      s"SSP: ${hexStr(cpu.getSSP)}",
      s"USP: ${hexStr(cpu.getUSP)}",
      f"SR : ${cpu.getSR}%04x $flagView"
    )

    val bps = mon.getBreaks.map(addr => f"$addr%08x").mkString(",")

    val stateInfo = List(
      s"Clk: ${mon.getTime}",
      s"Bps: ${bps}"
    )

    regText.setText(List(otherRegs, dregs, aregs, stateInfo).map(makeCols(if(isWide) 5 else 3, _))
      .mkString("\n\n"))
  }

  def isWide =
    cfg.orientation == Configuration.ORIENTATION_LANDSCAPE

  private val DIS = 1
  private val DUMP = 2
  private var dumpType = DIS

  def updateMemoryDump = {
    val sbuf = new scala.collection.mutable.ListBuffer[String]()

    if(dumpType == DIS) {
      val cpu = mon.cpu
      val pc = cpu.getPC
      var start = pc

      var count = 0
      val num_instructions = if(isWide) 5 else 10
      val memory = mon.addrSpace
      while(start < memory.size() && count < num_instructions) {
        val buffer = new java.lang.StringBuilder
        if(start == cpu.getPC)
          buffer.append("> ")
        else if(mon.hasBreak(start))
          buffer.append("! ")
        else
          buffer.append("  ")

        val opcode = cpu.readMemoryWord(start)
        val i = cpu.getInstructionFor(opcode)
        val di = i.disassemble(start, opcode)

        if(isWide) {
          di.formatInstruction(buffer)
        } else {
          di.shortFormat(buffer)
        }

        sbuf += buffer.toString()
        start += di.size()
        count += 1
      }
    }
    memText.setText(sbuf.toList.mkString("\n"))
  }

  def doStep(v: View): Unit = {
    mon.step
    update
  }

  def toastMsg(msg: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

  def enterButton(v: View): Unit = {
    /* we should run the command contained in the command input text field */
    val cmd = cmdInput.getText().toString().split(" ")
    cmd.headOption match {
      case None      => toastMsg("No command")
      case Some("")  => toastMsg("No command")
      case Some(cmdText) => doCmd(cmdText, cmd.tail)
    }
    cmdInput.getText().clear()
  }

  def parseNum(sOpt: Option[String]) = {
    sOpt map { s =>
      if(s.startsWith("$")) {
        Integer.parseInt(s.tail, 16)
      } else {
        s.toInt
      }
    }
  }

  def doCmd(cmd: String, args: Seq[String]) = {
    cmd.toLowerCase match {
      case "go" =>
        mon.execute
        toastMsg("complete")
        update
      case "b" =>
        parseNum(args.headOption) match {
          case None => toastMsg("Bad arg")
          case Some(addr) =>
            mon.addBreak(addr)
            toastMsg(f"added $addr%08x")
        }
        update
      case "view" =>
        doView
      case "clear" =>
        mon.clearBreaks
        update
      case "reset" =>
        mon.reset
        update
      case _ =>
        toastMsg(s"unknown cmd: $cmd")
    }
  }

  def doReset: Unit = {
    mon.cpu.reset
    update
    toastMsg("reset")
  }

  def doView: Unit = {
    try {
      val intent = new Intent(this, classOf[QLScreenActivity])
      startActivity(intent)
    } catch {
      case e: Exception => writeError(e.toString)
    }
  }

}
