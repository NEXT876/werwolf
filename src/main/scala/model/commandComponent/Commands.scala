// src/main/scala/model/commands/ExecuteC.scala
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.Game
import scala.util.{Try, Success, Failure}
import de.htwg.werwolf.controller.gameControllerComponent.GameCommand
import de.htwg.werwolf.model.gameCoreComponents.Votes
import de.htwg.werwolf.model.CommandInterface

case object NothingToUndo extends RuntimeException("Nichts zum Rückgängigmachen!")

case class ExecuteC() extends CommandInterface {
  def executeCommand(cmd: GameCommand, game : Game): Game =
    val updatedGame = cmd.execute(game)
    updatedGame.copy(commandHistory = updatedGame.commandHistory :+ cmd)

  def undoLast(game : Game): Try[Game] = Try {
    if (game.commandHistory.isEmpty) Failure(NothingToUndo)
    val cmd = game.commandHistory.last
    val revertedGame = cmd.undo(game)
    revertedGame.copy(commandHistory = revertedGame.commandHistory.init)
  }

  def replay(game : Game): Unit =
    println("=== REPLAY ===")
    game.commandHistory.reverse.foreach { cmd =>
      //println(s"• ${cmd.description}")
    }

  def createMemento(game : Game): GameMemento =
    GameMemento(
      players = game.players,
      phase = game.phase ,
      day = game.day ,
      votes = game.votes ,
      isRunning = game.isRunning,
      commandHistory = game.commandHistory.reverse
    )

  def restoreFromMemento(m: GameMemento, game : Game): Game =
      game.copy(
        players = m.players,
        phase = m.phase,
        day = m.day,
        votes = m.votes,
        isRunning = m.isRunning,
        commandHistory = m.commandHistory
      )

}