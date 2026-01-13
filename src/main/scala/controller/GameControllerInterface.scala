package de.htwg.werwolf.controller

import de.htwg.werwolf.model.{Game,GameEvent}
import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.commandComponent.GameCommand
import de.htwg.werwolf.model.Faction

trait GameControllerInterface extends Subject[GameEvent]:
    def saveGameState(): Unit
    def undoFull(): Unit
    def executeCommand(cmd: String, winner : Option[Faction], game : Game): Game
    def undoCommand(): Game
    def countAlivePlayer(): (Int, Int)
    def addRoles(names: Vector[String]): Unit
    def runGame(): Unit
     
    //def vote(voter: String, target: String): GameCommand 
    //def endGame(winner: Option[String]): GameCommand 
    