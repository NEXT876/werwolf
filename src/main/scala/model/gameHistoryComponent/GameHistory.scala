// src/main/scala/de/htwg/werwolf/gameHistory/gameHistoryInterface.scala
package de.htwg.werwolf.model.gameHistoryComponent

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.commandComponent.{CommandInterface, GameMemento}


case class GameHistory() extends gameHistoryInterface:
  private val saves = scala.collection.mutable.Stack[GameMemento]()

  def save(game: Game)(using ci : CommandInterface): Unit = {
    saves.push(ci.createMemento(game))
    //println(s"Spielstand ${saves.size} gespeichert")
  }

  def undo(game: Game)(using ci : CommandInterface): Unit = if (saves.nonEmpty) {
    ci.restoreFromMemento(saves.pop(), game)
    //println("Zurück zum letzten Savepoint!")
  }

  def list(): Unit = saves.zipWithIndex.reverse.foreach {
    case (m, i) =>// println(s"$i: ${m}")
  }
