lazy val http4sVersion = "0.14.2a"

lazy val root = (project in file(".")).settings(
  scalaVersion := "2.11.8",
  version := "1.0.0",
  name := "File Handles Test client",
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "org.scalaz.stream" %% "scalaz-stream" % "0.8.3a"
  ))
