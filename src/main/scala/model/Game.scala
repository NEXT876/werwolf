// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.*
import de.htwg.werwolf.util.Subject

import upickle.default.*
import scala.util.Random
import scala.collection.immutable.Vector
import scala.util.{Try, Success, Failure}


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


enum Roles:
  case werwolf, villager, terrorist, witch, amor
  def toPlayer(name: String): Player = this match
    case Roles.werwolf   => Werwolf(name)
    case Roles.villager  => Villager(name)
    case Roles.terrorist => Terrorist(name)
    case Roles.witch     => Witch(name)
    case Roles.amor      => Amor(name)

  override def toString(): String = this match
    case Roles.werwolf   => "Werwolf"
    case Roles.villager  => "Villager"
    case Roles.terrorist => "Terrorist"
    case Roles.witch     => "Witch"
    case Roles.amor      => "Amor"

case object NothingToUndo extends RuntimeException("Nichts zum Rückgängigmachen!")

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

  def narratorData () : Root =
    NarratorService.loadNarratorJson(os.pwd  / "src" / "main"/ "resources" / "narrator.json")

  def executeCommand(cmd: GameCommand): Game = {
    val updatedGame = cmd.execute(this)
    updatedGame.copy(commandHistory = commandHistory :+ cmd)
  }

  def undoLast(): Try[Game] = Try {
    if (commandHistory.isEmpty) Failure(NothingToUndo)
    val cmd = commandHistory.last
    val revertedGame = cmd.undo(this)
    revertedGame.copy(commandHistory = commandHistory.init)
  }
/*
  def replay(): Unit =
    println("=== REPLAY ===")
    commandHistory.reverse.foreach { cmd =>
      println(s"• ${cmd.description}")
    }*/


            // Memento //

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


  def addRoles(playerNames: Vector[String]): Game =
    val roles = getRoles(playerNames.size)

    // 1. Erst alle Spieler mit normalen Rollen erzeugen
    val basePlayers: Map[String, Player] = Random
      .shuffle(playerNames)
      .zip(roles)
      .map { case (name, role) =>
        val player = role.toPlayer(name)
        name -> player
      }
      .toMap

    // 2. decorater greift ein
    val updatedPlayers: Map[String, Player] = basePlayers.collectFirst {
      case (name, p) if !p.isInstanceOf[Werwolf] =>         
        name -> DoubleVoteDecorator(p)                       
    }.fold(basePlayers) { case (name, decoratedPlayer) =>     
      basePlayers.updated(name, decoratedPlayer)
    }

    copy(players = updatedPlayers)
    
  def getRoles(playeramount: Int): Vector[Roles] =
    if playeramount == 2 then Vector(Roles.werwolf, Roles.villager)
    else
      Vector.fill(playeramount / 3)(Roles.werwolf) ++ Random.shuffle(
        Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
      )

  def switchPhase(): Game =
    createMemento()
    val newPhase = if phase == Phase.Night then Phase.Day else Phase.Night
    val newDay = day + 1
    copy(phase = newPhase, day = newDay, votes = Votes())


  def runNightPhase(): Game = {
    val updatedGame = players.foldLeft(this) { case (g, (name, player)) =>
      player.nightAction.performAction(player, g)
    }
    updatedGame
  }

  def runDayPhase(): Unit = {
     val updatedGame = players.foldLeft(this) { case (g, (name, player)) =>
      player.nightAction.performAction(player, g)
    }
    updatedGame
  }


  def checkWinCondition(players: Map[String, Player]): Option[Faction] =
    val winners =
      Faction.values.filter(_.winCondition(players))

    if winners.size == 1 then Some(winners.head)
    else None

  object NarratorService:
    def loadNarratorJson(path: os.Path): Root =
      val jsonString = os.read(path)
      read[Root](jsonString)

    // Random ist jetzt Parameter!
    def randomNarratorText(role: String, root: Root) /*(using rnd: Random)*/: String =
      val list = role match
        case "Start"   => root.Night.Start
        case "Werwolf" => root.Night.Werwolf
        case "Witch"   => root.Night.Witch
        case "Amor"    => root.Night.Amor
        case _         => List("")
      /*rnd.*/
      Random.shuffle(list).headOption.getOrElse("")
}
