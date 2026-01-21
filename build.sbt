ThisBuild / scalaVersion := "3.7.3"

import sbtassembly.AssemblyPlugin.autoImport._

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "subtrate", _ @_*)     => MergeStrategy.discard
  case PathList("META-INF", "native-image", _ @_*) => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) =>
    xs.map(_.toLowerCase) match {
      case "manifest.mf" :: Nil => MergeStrategy.discard
      case _                    => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}

assembly / mainClass := Some("de.htwg.werwolf.Main")

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalafx-test",
    scalacOptions ++= Seq(
      "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s",
      "-deprecation",
      "-explain",
      "-feature",
      "-unchecked"
    ),
    javaOptions ++= Seq("-XX:+EnableDynamicAgentLoading"),

    // Aktuelle ScalaFX (für JavaFX 21)
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "com.lihaoyi" %% "upickle" % "4.0.0",
      "com.lihaoyi" %% "os-lib" % "0.9.1",
      "org.scalafx" %% "scalafx" % "20.0.0-R31",
      "com.google.inject" % "guice" % "7.0.0",
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.typesafe.play" %% "play-json" % "2.10.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
      "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0" % Test,
      "org.mockito" % "mockito-core" % "5.11.0" % Test
    ),

    // Cross-platform (Unix + Windows) — alle Dateien im Ordner `view` und Unterordner
    coverageExcludedFiles :=
      """.*[/\\]view[/\\].*;.*[/\\]fileIO[/\\].*;.*Main;.*config""",
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
