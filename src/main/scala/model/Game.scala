// src/main/scala/de/htwg/werwolf/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.controller.gameControllerComponent.GameCommand
import de.htwg.werwolf.model.gameCoreComponents.{Player, Votes}

import scala.util.{Try, Success, Failure}


import scala.util.{Try, Success, Failure}
import scala.util.Random

case class Game(
    players: Map[String, Player] = Map.empty,
    phase: Phase = Phase.Night,
    day: Int = 1,
    votes: Votes = Votes(),
    isRunning: Boolean = true,
    commandHistory: Vector[GameCommand] = Vector.empty
) {
  override def toString(): String = players.values.mkString("\n") + "\n"

  def checkWinCondition(players: Map[String, Player]): Option[Faction] =
    val winners =
      Faction.values.filter(_.winCondition(players))
    if winners.size == 1 then Some(winners.head)
    else None
}
