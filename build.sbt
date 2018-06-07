name := "MqttKafka"

fork := true
javaOptions in test ++= Seq(
  "-Xms128M", "-Xmx256M",
  "-XX:MaxPermSize=256M",
  "-XX:+CMSClassUnloadingEnabled"
)

parallelExecution in test := false

version := "1.0"

scalaVersion := "2.12.6"
val akkaVersion = "2.5.12"

libraryDependencies ++=
  Seq(

    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "com.typesafe" % "config" % "1.2.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

    "com.newrelic.agent.java" % "newrelic-api" % "3.47.0",

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    "com.typesafe.akka" %% "akka-stream-kafka" % "0.20",

    "com.sandinh" %% "paho-akka" % "1.5.0",

    "org.scalatest" %% "scalatest" % "3.0.5" % "test"

  )

mainClass in assembly := Some("onextent.iot.mqtt.kafka.Main")
assemblyJarName in assembly := "MqttKafka.jar"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

