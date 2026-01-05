package de.htwg.werwolf.narrator

import de.htwg.werwolf.narrator.*

import scala.util.Random
import upickle.default.*

class JsonNarrator(path: os.Path) extends Narrator {
  private val root = read[Root](os.read(path))

  def randomNarratorText(role: String): String = {
    val list = role match
      case "Start"   => root.Night.Start
      case "Werwolf" => root.Night.Werwolf
      case "Witch"   => root.Night.Witch
      case "Amor"    => root.Night.Amor
      case _         => List("")
    Random.shuffle(list).headOption.getOrElse("")
  }
}
