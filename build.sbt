scalaVersion in ThisBuild := "2.11.8"

val common = (project in file("common"))
  .disablePlugins(AndroidApp)
  .settings(exportJars := true,
    libraryDependencies += "com.typesafe" % "config" % "1.3.1")
  .settings(BuildRom.settings:_*)

val console = (project in file("console"))
  .disablePlugins(AndroidApp)
  .settings(libraryDependencies += "org.jline" % "jline" % "3.3.1")
  .dependsOn(common)

val android = (project in file("android"))
  .enablePlugins(AndroidApp)
  .dependsOn(common)
