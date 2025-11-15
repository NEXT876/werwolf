// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.*

import scala.util.Random


enum Phase:
  case Night, Day

enum Roles:
  case werwolf, villager, terrorist, witch, amor

  def toPlayer(name: String): Player = this match
    case Roles.werwolf   => Werwolf(name)
    case Roles.villager  => Villager(name)
    case Roles.terrorist => Terrorist(name)
    case Roles.witch     => Witch(name)
    case Roles.amor      => Amor(name)

case class GameState(
    day: Int,
    phase: Phase,
    votes: Votes,
    isRunning: Boolean,
    alivePlayers: Map[String, Player]
)

class Game() extends Subject[GameEvent] {
  private var players: Map[String, Player] = Map.empty
  private var phase: Phase = Phase.Night
  private var day: Int = 1
  private var votes: Votes = Votes()
  private var isRunning: Boolean = true

  def currentState: GameState =
    GameState(
      day = day,
      phase = phase,
      votes = votes, // Kopie, falls mutable
      isRunning = isRunning,
      alivePlayers = players.filter(_._2.isAlive).toMap
    )

  def addPlayers(newPlayers: Map[String, Player]): Unit =
    players = players ++ newPlayers
    notifyObservers(GameEvent.printGameState)

  def switchPhase(): Unit =
    phase = if phase == Phase.Night then Phase.Day else Phase.Night
    notifyObservers(GameEvent.phaseSwitch)

  def GameEnd() : Unit = 
    isRunning = false
    notifyObservers(GameEvent.gameEnd)

  object NarratorService:
    import upickle.default.*
    def loadNarratorJson(path: os.Path): Root =
      val jsonString = os.read(path)
      read[Root](jsonString)

    def randomNarratorText(role: String, root: Root): String =
      val list = role match {
        case "Start"   => root.Night.Start
        case "Werwolf" => root.Night.Werwolf
        case "Witch"   => root.Night.Witch
        case "Amor"    => root.Night.Amor
        case _         => List("")
      }
      util.Random.shuffle(list).head
}

/* def night(playerRoles: Map[String, Player], fakeInt: Int = 999): Map[String, Player] = {
    import scala.io.StdIn.readLine
    import scala.io.Source

    notifyObservers(currentState)

    val initialVotes = Votes()
    val (updatedRoles, finalVotes) = playerRoles.foldLeft(playerRoles, initialVotes) {
      case ((currentState, votesObject), (name, player)) =>
        if (player.role == "Werwolf" && player.isAlive) {
          notifyObservers(GameEvent.WerewolfTurn(name, currentState))
          if (fakeInt == 999) {
            val vote = readLine(s"Spieler $name, bitte geben sie an wen sie umbringen möchten: ")
            val voteText = player.vote(currentState(vote))
            print(s"\u001b[31m${voteText}\u001b[0m\n")
            val updatedVotes = votesObject.addVote(vote)
            (currentState, updatedVotes)
          } else {
            val updatedVotes = Votes(Map(name -> fakeInt))
            (currentState, updatedVotes)
          }
        } else {
          (currentState, votesObject)
        }
    }
    finalVotes.getVotedPlayer match {
      case Some(p) =>
        val updatedPlayer = updatedRoles(p).die
        val newRoles = updatedRoles.updated(p, updatedPlayer)
        newRoles // diese Map zurückgeben
      case None =>
        updatedRoles
    }

  }
 */
