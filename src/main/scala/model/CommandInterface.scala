// src/main/scala/model/commands/CommandInterface.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.commandComponent.{GameCommand, GameMemento}

import scala.util.{Try, Success, Failure}

trait CommandInterface {
    def executeCommand(cmd: GameCommand, game : Game): Game
    def undoLast(game : Game): Try[Game]
    def replay(game : Game): Unit
    def createMemento(game : Game): GameMemento
    def restoreFromMemento(m: GameMemento, game : Game): Game
}