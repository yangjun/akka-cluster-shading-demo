name := """akka-sharding"""

version := "1.0"

scalaVersion := "2.10.4"

val akkaVersion = "2.3.9"

javacOptions in (Compile, compile) ++= Seq("-source", "1.6", "-encoding", "UTF-8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

enablePlugins(JavaServerAppPackaging)
