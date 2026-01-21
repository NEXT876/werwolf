package de.htwg.werwolf.model.narratorComponent

import de.htwg.werwolf.model.NarratorInterface
import scala.util.{Random, Using}
import scala.io.Source
import upickle.default.*

class JsonNarrator extends NarratorInterface {

  private val root: Root =
    Using.resource(
      Source.fromInputStream(
        getClass.getResourceAsStream("/narrator.json")
      )
    )(src => read[Root](src.mkString))

  def randomNightNarratorTexte(role: String): String =
    val list = role match
      case "Start"   => root.Night.Start
      case "Werwolf" => root.Night.Werwolf
      case "Witch"   => root.Night.Witch
      case "Amor"    => root.Night.Amor
      case _         => Nil
    Random.shuffle(list).headOption.getOrElse("")

  def randomDayNarratorTexte(role: String): String =
    val list = role match
      case "Start" => root.Day.Start
      case _       => Nil
    Random.shuffle(list).headOption.getOrElse("")
}
