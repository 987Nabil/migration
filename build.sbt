ThisBuild / scalaVersion     := "3.2.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "migration"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "migration",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.2",
      "dev.zio" %% "zio-test" % "2.0.2" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
