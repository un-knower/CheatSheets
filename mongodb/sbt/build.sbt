name := "MyProject"

version := "1.0"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
    "org.mongodb" %% "casbah" % "2.6.0",
    "org.slf4j" % "slf4j-simple" % "1.6.4"
)

scalacOptions += "-deprecation"
