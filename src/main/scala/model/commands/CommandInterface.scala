package de.htwg.werwolf.model.commands
import de.htwg.werwolf.model.Game
import scala.util.{Try, Success, Failure}


trait CommandInterface {
    def executeCommand(cmd: GameCommand, game : Game): Game
    def undoLast(game : Game): Try[Game]
    def replay(game : Game): Unit
}
