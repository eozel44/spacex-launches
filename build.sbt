import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / semanticdbEnabled := true


lazy val scalacopts = Seq(
  "-feature",
  "-deprecation",
  "-encoding","UTF-8",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Wunused:imports",
  "-Ymacro-annotations"
)

lazy val root = (project in file("."))
  .settings(
    name := "spacex-launches",
    libraryDependencies ++= Seq(
      zio,
      scalaTest % Test,
      scalaj
    ) ++ circe ++ zioLogging,
    scalacOptions ++= scalacopts
  )