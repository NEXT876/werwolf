// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*

import scala.util.Random

enum Role:
  case werwolf, villager, terrorist, witch, amor

  def toPlayer(name: String): Player = this match
    case Role.werwolf   => Werwolf(name)
    case Role.villager  => Villager(name)
    case Role.amor      => Amor(name)
    case Role.terrorist => Terrorist(name)
    case Role.witch     => Witch(name)

class GameController(game: Game) {

  def addRoles(players: Vector[String]): Unit = {
    val roles = getRoles(players.size)

    //1. Möglichkeit auslagern
    // game.generatePlayerList(Random.shuffle(players), roles)

    //2. Möglichkeit hier lassen und rüber speichern am Ende
    val playerMap = Random
      .shuffle(players)
      .zip(roles)
      .map { case (name, role) =>
        val player = role.toPlayer(name)
        player.name -> player
      }
      .toMap

    //dann sowas, addPlayer gibt es halt noch nicht
    //playerMap.values.foreach(game.addPlayer)
  }

  def getRoles(playeramount: Int): Vector[Role] = {
    if playeramount == 2 then Vector(Role.werwolf, Role.villager)
    else
      Vector.fill(playeramount / 3)(Role.werwolf) ++ Random.shuffle(
        Vector(Role.villager, Role.witch, Role.amor, Role.terrorist)
      )
  }

}
