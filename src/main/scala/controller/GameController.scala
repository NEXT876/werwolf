// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*

import scala.util.Random

class GameController( /*private val */ game: Game) {
  // def getGame: Game = game

  def initializePlayers(players: Vector[String]): Unit = {
    game.addRoles(players)
  }

  def process(input: String): Unit =
    input match
      case "switchPhase" => game.switchPhase()
      case "runPhase"    => game.runPhase()
      case "GameEnd"     => game.GameEnd()
      case _             =>
}
