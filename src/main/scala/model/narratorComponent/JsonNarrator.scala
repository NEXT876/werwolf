// src/main/scala/model/narrator/JsonNarrator.scala
package de.htwg.werwolf.model.narratorComponent

import scala.util.Random
import upickle.default.*

class JsonNarrator(path: os.Path) extends NarratorInterface {
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
