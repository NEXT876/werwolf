// src/main/scala/narrator/narrator.scala

package de.htwg.werwolf.narrator

import upickle.default._

case class Night(
    Start: List[String],
    Werwolf: List[String],
    Amor: List[String],
    Witch: List[String]
)

case class Root(Night: Night)

object Night {
  implicit val rw: ReadWriter[Night] = macroRW
}

object Root {
  implicit val rw: ReadWriter[Root] = macroRW
}

def randomNarratorText(Rolle: String): String =
  val jsonString = os.read(os.pwd / "src" / "main" / "resources" / "narrator.json")
  val data = read[Root](jsonString)
  val text = Rolle match {
    case "Start"   => data.Night.Start
    case "Werwolf" => data.Night.Werwolf
    case "Witch"   => data.Night.Witch
    case "Amor"    => data.Night.Amor
    case _         => List[String]("")
  }
  val randomText = util.Random.shuffle(text).head
  randomText
