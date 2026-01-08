package de.htwg.werwolf.model.commands
import de.htwg.werwolf.model.Game
import scala.util.{Try, Success, Failure}

case object NothingToUndo extends RuntimeException("Nichts zum Rückgängigmachen!")

case class ExecuteC() extends CommandInterface {
//componente execution and save game state
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
}