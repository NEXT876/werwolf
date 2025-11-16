// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*

import scala.util.Random

class GameController(private val game: Game) {
  def getGame: Game = game

  def addRoles(players: Vector[String]): Unit = {
    val roles = getRoles(players.size)

    val playerMap = Random
      .shuffle(players)
      .zip(roles)
      .map { case (name, role) =>
        val player = role.toPlayer(name)
        player.name -> player
      }
      .toMap

    game.addPlayers(playerMap)
  }

  def getRoles(playeramount: Int): Vector[Roles] = {
    if playeramount == 2 then Vector(Roles.werwolf, Roles.villager)
    else
      Vector.fill(playeramount / 3)(Roles.werwolf) ++ Random.shuffle(
        Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
      )
  }

  def process(input: String): Unit =
    input match
      case "switchPhase" => game.switchPhase()
      //
      case "GameEnd" => game.GameEnd()
}
