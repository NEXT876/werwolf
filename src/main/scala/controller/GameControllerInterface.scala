package de.htwg.werwolf.controller

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.util.Observer
import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.util.Subject

trait GameControllerInterface extends Subject[GameEvent]:
    def saveGameState(): Unit
    def undoFull(): Unit
    def executeCommand(cmd: GameCommand, game : Game): Game
    def undoCommand(): Game
    def countAlivePlayer(): (Int, Int)
    def addRoles(names: Vector[String]): Unit
    def runGame(): Unit