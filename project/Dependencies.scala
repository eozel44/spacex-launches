import sbt._

object Versions {
  val scala2                  = "2.13.8"
  val zio                     = "1.0.11"
  val zioConfig               = "3.0.2"
  val scalatest               = "3.2.15"
  val circe                   = "0.14.2"
  val zioLogging              = "0.5.10"
}

object Dependencies {
  lazy val scalaTest  = "org.scalatest" %% "scalatest"    % Versions.scalatest
  lazy val zio        = "dev.zio"       %% "zio"          % Versions.zio

  val zioLogging = Seq(
    "dev.zio" %% "zio-logging"       % Versions.zioLogging,
    "dev.zio" %% "zio-logging-slf4j" % Versions.zioLogging
  )

  lazy val circe      = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.circe)

  lazy val scalaj = "org.scalaj"       %% "scalaj-http"    % "2.4.2"
}
