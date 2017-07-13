package smsqmulator.android

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.graphics.drawable.Animatable

import java.io.{ PrintWriter, ByteArrayOutputStream }

import m68k.cpu.Cpu

class MonitorActivity extends AppCompatActivity with TypedFindView {
  // allows accessing `.value` on TR.resource.constants
  implicit val context = this

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
    val ncols = 3
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

    regText.setText(List(otherRegs, dregs, aregs).map(makeCols(3, _)).mkString("\n\n"))
  }

  def updateMemoryDump = {
    val cpu = mon.cpu
    var start = cpu.getPC

    var count = 0
    val num_instructions = 5
    val memory = mon.addrSpace
    val buffer = new java.lang.StringBuilder

    val sbuf = new scala.collection.mutable.ListBuffer[String]()

    while(start < memory.size() && count < num_instructions) {
      buffer.delete(0, buffer.length());

      val opcode = cpu.readMemoryWord(start)
      val i = cpu.getInstructionFor(opcode)
      val di = i.disassemble(start, opcode)
      // if(showBytes)
      // {
      //   di.formatInstruction(buffer);
      // }
      // else
      // {

      di.shortFormat(buffer);

      sbuf += buffer.toString()
      start += di.size()
      count += 1
    }

    memText.setText(sbuf.toList.mkString("\n"))
  }

}
