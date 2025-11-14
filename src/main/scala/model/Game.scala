// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.model.{Amor, Player, Terrorist, Villager, Werwolf, Witch}
import de.htwg.werwolf.model.Votes
import de.htwg.werwolf.model.Observer
import de.htwg.werwolf.model.Subject

import java.util.concurrent.atomic.AtomicInteger
import scala.util.Random

class Game extends Subject {

  object GlobalDayCounter {
    private val day = new AtomicInteger(0)

    def increment(): Int = day.incrementAndGet()
    def get(): Int = day.get()
    def reset(): Unit = day.set(0)
  }

  enum Roles:
    case werwolf
    case villager
    case amor
    case terrorist
    case witch

    def toPlayer(name: String): Player = this match
      case Roles.werwolf   => Werwolf(name)
      case Roles.villager  => Villager(name)
      case Roles.amor      => Amor(name)
      case Roles.terrorist => Terrorist(name)
      case Roles.witch     => Witch(name)

  def addRoles(players: Vector[String]): Map[String, Player] = {
    if players.size == 2
    then // sorgt dafür dass wenn es nur 2 Spieler gibt es immer 1 Werwolf und 1 Villager sind(sonst unnötig)
      val roles = Vector(Roles.werwolf, Roles.villager)
      val shuffeledRoles = Random.shuffle(roles)
      (players zip shuffeledRoles).map { case (name, role) =>
        val player = role.toPlayer(name)
        player.name -> player
      }.toMap
    else
      val werwolfAmount = if players.size <= 3 then 1 else 2
      val roles = Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
      val shuffeledRoles = Random.shuffle(roles)
      val finalRoles: Vector[Roles] = (Vector.fill(werwolfAmount)(Roles.werwolf)
        ++ shuffeledRoles).take(players.size)
      (players zip finalRoles).map { case (name, role) =>
        val player = role.toPlayer(name)
        player.name -> player
      }.toMap
  }

  def night(playerRoles: Map[String, Player], fakeInt: Int = 999): Map[String, Player] = {
    import scala.io.StdIn.readLine
    import scala.io.Source

    notifyObservers(NightPhaseStarted(playerRoles))

    val initialVotes = Votes()
    val (updatedRoles, finalVotes) = playerRoles.foldLeft(playerRoles, initialVotes) {
      case ((currentState, votesObject), (name, player)) =>
        if (player.role == "Werwolf" && player.isAlive) {
          notifyObservers(WerewolfTurn(name, currentState))
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

  object NarratorService:

    def loadNarratorJson(path: os.Path): Root =
      val jsonString = os.read(path)
      read[Root](jsonString)

    def randomNarratorText(Rolle: String): String =
      val jsonString = os.read(os.pwd / "src" / "main" / "resources" / "narrator.json")
      val data = read[Root](jsonString)
      val text = Rolle match {
        case "Start"   => data.Night.Start
        case "Werwolf" => data.Night.Werwolf
        case "Witch"   => data.Night.Witch
        case "Amor"    => data.Night.Amor
        case _         => List[String]("")
      }
      val randomText = util.Random.shuffle(text).head
      randomText
}
