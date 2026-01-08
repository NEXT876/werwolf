package de.htwg.werwolf.model.Command
import de.htwg.werwolf.model.Game
import scala.util.{Try, Success, Failure}


trait CommandInterface {
    def executeCommand(cmd: GameCommand): Game
    def undoLast(): Try[Game]
    def replay(): Unit
}