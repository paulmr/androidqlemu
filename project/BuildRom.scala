import sbt._
import Keys._

object BuildRom {
  val asmSourceDirectory = Def.settingKey[File]("location of the asm files")
  val asmSourceFile = Def.settingKey[File]("file to assemble")
  val assemblerOutput = Def.settingKey[File]("where should the files go")
  val assembleFiles = Def.taskKey[Unit]("assemble the files")

  val settings = Seq(
    asmSourceDirectory := {
      sourceDirectory.value / "main" / "asm"
    },
    asmSourceFile := sourceDirectory.value / "main" / "asm" / "qlemu.asm",
    assemblerOutput := sourceDirectory.value / "main" / "res" / "raw" / "qlemurom",
    assembleFiles := {
      if(assemblerOutput.value olderThan asmSourceFile.value) {
        streams.value.log.info("Assembling")
        val res = Process(
          Seq("asmx", "-C68000", "-b", "-e", "-o",
            assemblerOutput.value.toString,
            asmSourceFile.value.toString
          ),
          asmSourceDirectory.value
        ).!
        if(res != 0) sys.error("Assembler failed")
      }
    },
    compile in Compile := ((compile in Compile) dependsOn assembleFiles).value
  )
}
