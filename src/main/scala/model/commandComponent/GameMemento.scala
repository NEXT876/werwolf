// src/main/scala/de/htwg/werwolf/model/commandComponent/GameMemento
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.gameCoreComponents.{Player, Votes}
import de.htwg.werwolf.model.{Phase,Game}
import de.htwg.werwolf.model.CommandInterface

import play.api.libs.json._

case class GameMemento(
    players: Map[String, Player],
    phase: Phase,
    day: Int,
    votes: Votes,
    isRunning: Boolean,
    @transient commandHistory: Vector[GameCommand] = Vector.empty
) {
  // ignored by Play JSON
  @transient
  private val saves = scala.collection.mutable.Stack[GameMemento]()

  def save(game: Game)(using ci: CommandInterface): Unit = {
    saves.push(ci.createMemento(game))
    // println(s"Spielstand ${saves.size} gespeichert")
    // TODO write into file
  }

  def undo(game: Game)(using ci: CommandInterface): Unit = if (saves.nonEmpty) {
    ci.restoreFromMemento(saves.pop(), game)
    // println("Zurück zum letzten Savepoint!")
    // read from file if needed
  }

  def list(): Unit = saves.zipWithIndex.reverse.foreach { case (m, i) => // println(s"$i: ${m}")
  }
}
