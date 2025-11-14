// src/main/scala/controller/GameController.scala
package de.htwg.werwolf.controller

import de.htwg.werwolf.model.*
import de.htwg.werwolf.view.*

class GameController(game: Game) extends Subject {

  def startGame(players: Vector[String]): Unit =
    val roles = game.addRoles(players)
    val updated = game.night(roles)

    // UI übernimmt Anzeige
    notifyObservers(GameEvent.GameStart(updated))

  def update(event: GameEvent): Unit = event match {
    case GameEvent.NightPhaseStarted(roles)  => {}
    case GameEvent.WerewolfTurn(name, roles) => {}
    case GameEvent.GameStart(roles)          => {}
  }
}
