// src/main/scala/model/narrator/NarratorModel.scala
package de.htwg.werwolf.model.narratorComponent

import upickle.default.*

case class Night(
    Start: List[String],
    Werwolf: List[String],
    Amor: List[String],
    Witch: List[String]
) derives ReadWriter

case class Day(Start: List[String]) derives ReadWriter

case class Root(Night: Night, Day : Day) derives ReadWriter
