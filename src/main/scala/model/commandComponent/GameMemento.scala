// src/main/scala/de/htwg/werwolf/model/commandComponent/GameMemento
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.gameCoreComponents.{Player, Votes}
import de.htwg.werwolf.controller.gameControllerComponent.GameCommand
import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.CommandInterface
import de.htwg.werwolf.model.Phase

case class GameMemento(
    players: Map[String, Player],
    phase: Phase,
    day: Int,
    votes: Votes,
    isRunning: Boolean,
    commandHistory: Vector[GameCommand]
) {
  private val saves = scala.collection.mutable.Stack[GameMemento]()

  def save(game: Game)(using ci: CommandInterface): Unit = {
    saves.push(ci.createMemento(game))
    // println(s"Spielstand ${saves.size} gespeichert")
  }

  def undo(game: Game)(using ci: CommandInterface): Unit = if (saves.nonEmpty) {
    ci.restoreFromMemento(saves.pop(), game)
    // println("Zurück zum letzten Savepoint!")
  }

  def list(): Unit = saves.zipWithIndex.reverse.foreach { case (m, i) => // println(s"$i: ${m}")
  }
}
