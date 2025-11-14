// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.Root
import scala.util.Random

private var players: List[Player] = Nil
private var phase: Phase = Phase.Night

case class GameState(
    players: Map[String, Player], // Name -> Player (Werwolf, Villager, etc.)
    day: Int = 1,
    phase: Phase = Phase.Night, // Day, Night, Voting
    votes: Votes = Votes(), // Aktuelle Abstimmungen
    isRunning: Boolean = true
) {}

enum Role:
  case Villager, Werwolf, Terrorist

enum Phase:
  case Night, Day


class Game() extends Subject {


  def night(playerRoles: Map[String, Player], fakeInt: Int = 999): Map[String, Player] = {
    import scala.io.StdIn.readLine
    import scala.io.Source

    notifyObservers(GameEvent.NightPhaseStarted(playerRoles))

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
