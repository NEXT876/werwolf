ThisBuild / scalaVersion := "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalafx-test",

    scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s",

    // Aktuelle ScalaFX (für JavaFX 21)
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "com.lihaoyi" %% "upickle" % "4.0.0",
      "com.lihaoyi" %% "os-lib" % "0.9.1",
      "org.scalafx" %% "scalafx" % "20.0.0-R31",
      "net.codingwell" %% "scala-guice" % "7.0.0"
    ),
    libraryDependencies ++= {
      val os = System.getProperty("os.name").toLowerCase match {
        case mac if mac.contains("mac") => "mac"
        case win if win.contains("win") => "win"
        case _                          => "linux"
      }
      Seq("base", "controls", "fxml", "graphics", "media", "web") // <-- media hinzugefügt
        .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier os)
    }
  )
