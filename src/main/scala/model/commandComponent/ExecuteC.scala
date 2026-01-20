// src/main/scala/model/commands/ExecuteC.scala
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.{Game, GameCoreInterface, CommandInterface}

import scala.util.{Try, Success, Failure}
import de.htwg.werwolf.model.Faction

case object NothingToUndo extends RuntimeException("Nichts zum Rückgängigmachen!")

case class ExecuteC() extends CommandInterface {

  private val saves = scala.collection.mutable.Stack[GameMemento]()

  def save(game: Game)(using ci: CommandInterface): Unit = {
    saves.push(ci.createMemento(game))
  }

  def undo(game: Game)(using ci: CommandInterface): Unit = if (saves.nonEmpty) {
    ci.restoreFromMemento(saves.pop(), game)
    // println("Zurück zum letzten Savepoint!")
  }

  def executeCommand(cmd: GameCommand, game: Game): Game =
    val updatedGame = cmd.execute(game)
    updatedGame.copy(commandHistory = updatedGame.commandHistory :+ cmd)

  def undoLast(game: Game): Try[Game] = Try {
    if (game.commandHistory.isEmpty) Failure(NothingToUndo)
    val cmd = game.commandHistory.last
    val revertedGame = cmd.undo(game)
    revertedGame.copy(commandHistory = revertedGame.commandHistory.init)
  }


  def createMemento(game: Game): GameMemento =
    GameMemento(
      players = game.players,
      phase = game.phase,
      day = game.day,
      votes = game.votes,
      isRunning = game.isRunning,
      commandHistory = game.commandHistory.reverse
    )

  def restoreFromMemento(m: GameMemento, game: Game): Game =
    game.copy(
      players = m.players,
      phase = m.phase,
      day = m.day,
      votes = m.votes,
      isRunning = m.isRunning,
      commandHistory = m.commandHistory
    )
}
