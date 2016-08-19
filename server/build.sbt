lazy val http4sVersion = "0.14.2a"

lazy val root = (project in file(".")).settings(
  scalaVersion := "2.11.8",
  version := "1.0.0",
  name := "File Handlers Test Server",
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-argonaut" % http4sVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.7"
  ))
