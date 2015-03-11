import play.PlayJava

name := """resty"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.wordnik" %% "swagger-play2" % "1.3.12",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "redis.clients" % "jedis" % "2.6.2"
)

javaOptions in Test += "-Dlogger.file=conf/test-logger.xml"

fork in run := true