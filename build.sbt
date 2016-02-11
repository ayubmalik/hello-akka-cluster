name := """hello-akka-cluster"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions += "-deprecation"

artifactName := { (version, module, artifact) =>  "helloAkkaCluster.jar" }

assemblyJarName in assembly := "helloAkkaRemoteApp.jar"

mainClass in assembly := Some("HelloClusterApp")
//mainClass in assembly := Some("HelloRemoteApp")


EclipseKeys.withSource := true

val akkaVersion = "2.4.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
  "com.typesafe.akka" %% "akka-remote"     % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"    % akkaVersion,
  "org.scalatest"     %% "scalatest"       % "2.2.6"       % "test",
  "junit"              % "junit"           % "4.12"        % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

fork in run := true

connectInput in run := true
