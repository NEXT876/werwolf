val scala3Version = "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "werwolf",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    coverageEnabled := true,
    coverageExcludedPackages := "upickle.*;de\\.htwg\\.werwolf\\.narrator.*",
    
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "com.lihaoyi" %% "upickle" % "4.0.0",
      "com.lihaoyi" %% "os-lib" % "0.9.1"
    )
  )
