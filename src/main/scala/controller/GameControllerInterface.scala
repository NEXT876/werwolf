//src/main/scala/controller/GameControllerInterface.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.{Game,GameEvent}
import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.commandComponent.GameCommand


trait GameControllerInterface extends Subject[GameEvent]:
    def saveGameState(): Unit
    def undoFull(): Unit
    def executeCommand(cmd: GameCommand, game : Game): Game
    def undoCommand(): Game
    def countAlivePlayer(): (Int, Int)
    def addRoles(names: Vector[String]): Unit
    def runGame(): Unit
    def submitNightChoice(playerName: String, target: String): Unit 
    def submitvoting(playerName: String, target: String): Unit 

