scalaVersion in ThisBuild := "2.11.8"

val common = (project in file("common"))
  .settings(exportJars := true)
  .settings(BuildRom.settings:_*)

val console = (project in file("console"))
  .settings(libraryDependencies += "org.jline" % "jline" % "3.3.1")
  .dependsOn(common)

val android = (project in file("android"))
  .dependsOn(common)
