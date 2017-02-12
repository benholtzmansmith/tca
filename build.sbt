name := "tca"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.2"

Defaults.itSettings

lazy val `it-config-sbt-project` = project.in(file(".")).configs(IntegrationTest)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-twitter" % sparkVersion,
  "com.github.finagle" %% "finch-core" % "0.11.0-M2" changing(),
  "com.github.finagle" %% "finch-argonaut" % "0.11.0-M2" changing(),
  "io.argonaut" %% "argonaut" % "6.1",
  "com.typesafe" % "config" % "1.3.0",
  "com.github.finagle" %% "finch-test" % "0.11.0-M2" % "test,it" changing(),
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test,it",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test,it"
)

