scalaVersion in ThisBuild := "2.11.8"

val common = (project in file("common"))
  .disablePlugins(AndroidApp)
  .settings(exportJars := true)
  //.settings(BuildRom.settings:_*)

val console = (project in file("console"))
  .disablePlugins(AndroidApp)
  .dependsOn(common)

val android = (project in file("android"))
  .enablePlugins(AndroidApp)
  .dependsOn(common)
