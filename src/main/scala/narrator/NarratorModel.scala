// src/main/scala/narrator/Narrator.scala
package de.htwg.werwolf.narrator

import upickle.default.*

case class Night(
  Start: List[String],
  Werwolf: List[String],
  Amor: List[String],
  Witch: List[String]
) derives ReadWriter

case class Root(Night: Night) derives ReadWriter