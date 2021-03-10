lazy val root = project
  .in(file("."))
  .settings(
    name := "zivver-assignment",
    description := "zivver assignment solution",
    version := "0.1.0",
    scalaVersion := "3.0.0-RC1",
    fork := true //this will make sure we run all our threads in a saperate JVM
  )

libraryDependencies ++= Seq (
  "org.scalactic" %% "scalactic" % "3.2.5",
  "org.scalatest" %% "scalatest" % "3.2.5" % "test"
)