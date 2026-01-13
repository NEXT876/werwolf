// src/main/scala/model/commands/CommandInterface.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.model.Game
import scala.util.{Try, Success, Failure}
import de.htwg.werwolf.model.commandComponent.GameCommand
import de.htwg.werwolf.model.commandComponent.GameMemento


trait CommandInterface {
    def executeCommand(cmd: String, winner : Option[Faction], killer : String,target : String, game : Game): Game
    def undoLast(game : Game): Try[Game]
    def replay(game : Game): Unit
    def createMemento(game : Game): GameMemento
    def restoreFromMemento(m: GameMemento, game : Game): Game
}