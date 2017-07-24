import sbt._
import Keys._

// uses the wonderful 'asmx' (http://xi6.com/projects/asmx/) to build
// the rom I'd really like to get a jvm m68k assembler to make this
// not require any external deps, but this assembler is really easy to
// compile and it is excellent so it'll do for now.

object BuildRom {
  val asmSourceDirectory = Def.settingKey[File]("location of the asm files")
  val asmSourceFile = Def.settingKey[File]("file to assemble")
  val assemblerOutput = Def.settingKey[File]("where should the files go")
  val assemblerListingsFile = Def.settingKey[File]("listings file")
  val assembleFiles = Def.taskKey[Unit]("assemble the files")

  val settings = Seq(
    asmSourceDirectory := {
      sourceDirectory.value / "main" / "asm"
    },
    asmSourceFile := sourceDirectory.value / "main" / "asm" / "qlemu.asm",
    assemblerOutput := sourceDirectory.value / "main" / "res" / "raw" / "qlemurom",
    assemblerListingsFile := {
      val p = baseDirectory.value / "target" / "asm"
      val f = p / "qlemurom.listing"
      if(!p.exists()) p.mkdirs()
      f
    },
    assembleFiles := {
      if(assemblerOutput.value olderThan asmSourceFile.value) {
        streams.value.log.info("Assembling")
        val res = Process(
          Seq("asmx", "-C68000", "-b", "-e",
            "-l", assemblerListingsFile.value.toString,
            "-o", assemblerOutput.value.toString,
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
