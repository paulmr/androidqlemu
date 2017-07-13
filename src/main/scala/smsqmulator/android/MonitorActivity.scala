package smsqmulator.android

import android.content.res.Configuration
import android.view.View
import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.graphics.drawable.Animatable

import java.io.{ PrintWriter, ByteArrayOutputStream }

import m68k.cpu.Cpu

class MonitorActivity extends AppCompatActivity with TypedFindView {
  // allows accessing `.value` on TR.resource.constants
  implicit val context = this
  lazy val cfg = context.getResources.getConfiguration

  lazy val regText = findView(TR.regText)
  lazy val memText = findView(TR.memText)

  lazy val mon =
    new qdos.QDOSMonitor(romFile = context.getResources.openRawResource(TR.raw.rom.resid))

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

    regText.setText(List(otherRegs, dregs, aregs).map(makeCols(if(isWide) 5 else 3, _))
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
        if(start == cpu.getPC) buffer.append("> ") else buffer.append("  ")

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
    val time = mon.cpu.execute
    // toast it?
    update
  }

  def doReset(v: View): Unit = {
    mon.cpu.reset
    update
  }

}
