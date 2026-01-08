// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.commandComponent.GameCommand
import de.htwg.werwolf.model.playerComponent.Player
import de.htwg.werwolf.model.voteComponent.Votes
import de.htwg.werwolf.model.phaseComponent.Phase

import scala.util.{Try, Success, Failure}
import scala.util.Random


case class Game (
  players: Map[String, Player] = Map.empty,
  phase: Phase = Phase.Night,
  day: Int = 1,
  votes: Votes = Votes(),
  isRunning: Boolean = true,
  commandHistory: Vector[GameCommand] = Vector.empty
)  {
  override def toString(): String = players.values.mkString("\n")+"\n"

  def checkWinCondition(players: Map[String, Player]): Option[Faction] =
    val winners =
      Faction.values.filter(_.winCondition(players))

    if winners.size == 1 then Some(winners.head)
    else None
}
