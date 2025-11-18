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
