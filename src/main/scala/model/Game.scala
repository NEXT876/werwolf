// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.model.commands.GameCommand
import de.htwg.werwolf.model.commands.CommandInterface
import scala.util.{Try, Success, Failure}
import scala.util.Random
import de.htwg.werwolf.model.roleUtils.{Amor, Player, Terrorist, Villager, Werwolf, Witch}

enum Phase:
  case Night, Day

enum Faction:
  case _Werwolf, _Villager
  def winCondition(players: Map[String, Player]): Boolean = this match
      case Faction._Werwolf =>
        val alive = players.values.filter(_.isAlive)
        alive.nonEmpty && alive.forall(p => p.faction == Faction._Werwolf)
      case Faction._Villager =>
        val alive = players.values.filter(_.isAlive)
        alive.nonEmpty && alive.forall(p => p.faction != Faction._Werwolf)

  override def toString(): String = this match
    case Faction._Werwolf  => "Werwölfe"
    case Faction._Villager => "Villager"

case class GameMemento(
  players: Map[String, Player],
  phase: Phase,
  day: Int,
  votes: Votes,
  isRunning: Boolean,
  commandHistory: Vector[GameCommand]
)

case class Game (
  players: Map[String, Player] = Map.empty,
  phase: Phase = Phase.Night,
  day: Int = 1,
  votes: Votes = Votes(),
  isRunning: Boolean = true,
  commandHistory: Vector[GameCommand] = Vector.empty
)  {
  override def toString(): String = players.values.mkString("\n")+"\n"

  def createMemento(): GameMemento =
    GameMemento(
      players = players,
      phase = phase ,
      day = day ,
      votes = votes ,
      isRunning = isRunning,
      commandHistory = commandHistory.reverse
    )

  def restoreFromMemento(m: GameMemento): Game =
      copy(
        players = m.players,
        phase = m.phase,
        day = m.day,
        votes = m.votes,
        isRunning = m.isRunning,
        commandHistory = m.commandHistory
      )

//componente Util
  def switchPhase(): Game =
    createMemento()
    val newPhase = if phase == Phase.Night then Phase.Day else Phase.Night
    val newDay = day + 1
    copy(phase = newPhase, day = newDay, votes = Votes())

  def runNightPhase(): Game =
    val updatedGame = players.foldLeft(this) { case (g, (name, player)) =>
      player.nightAction.performAction(player, g)(using ci: CommandInterface)
    }
    updatedGame

  def runDayPhase(): Game =
     val updatedGame = players.foldLeft(this) { case (g, (name, player)) =>
      player.nightAction.performAction(player, g)(using ci: CommandInterface)
    }
      updatedGame

  def checkWinCondition(players: Map[String, Player]): Option[Faction] =
    val winners =
      Faction.values.filter(_.winCondition(players))

    if winners.size == 1 then Some(winners.head)
    else None
}
