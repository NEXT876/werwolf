// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.*

import upickle.default.*
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



case class Game (
    players: Map[String, Player] = Map.empty,
    phase: Phase = Phase.Night,
    day: Int = 1,
    votes: Votes = Votes(),
    isRunning: Boolean = true
) extends Subject[GameEvent] {

              //command pattern//
  private val commandHistory = scala.collection.mutable.Stack[GameCommand]()

  def executeCommand(cmd: GameCommand): Unit = {
    cmd.execute()
    commandHistory.push(cmd)
  }

  def undoLast(): Unit = if (commandHistory.nonEmpty) {
    commandHistory.pop().undo()
    println("Letzte Aktion rückgängig gemacht!")
  }

  def replay(): Unit = {
    println("=== REPLAY ===")
    commandHistory.reverse.foreach { cmd =>
      println(cmd.description)
    }
  }
                //
                //


  def addRoles(playerNames: Vector[String]): Game = {
    val roles = getRoles(playerNames.size)

    val newPlayers = Random
      .shuffle(playerNames)
      .zip(roles)
      .map { case (name, role) =>
        val player = role.toPlayer(name)
        player.name -> player
      }
      .toMap

    notifyObservers(GameEvent.printGameState(newPlayers))
    copy(players = newPlayers)
  }

  def getRoles(playeramount: Int): Vector[Roles] = {
    if playeramount == 2 then Vector(Roles.werwolf, Roles.villager)
    else
      Vector.fill(playeramount / 3)(Roles.werwolf) ++ Random.shuffle(
        Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
      )
  }

  def switchPhase(): Game =
    val newPhase = if phase == Phase.Night then Phase.Day else Phase.Night
    val newDay = day + 1
    notifyObservers(GameEvent.phaseSwitch(newPhase))
    copy(phase = newPhase, day = newDay, votes = Votes())

  def runPhase(): Unit = {
    if phase == Phase.Night then runNightPhase()
    else runDayPhase()
  }

  def runNightPhase(): Unit = {
    println("Es ist Nacht")
    players.foreach { (name, player) => player.nightAction.performAction(player, this)}
    /** */
  }

  def runDayPhase(): Unit = {
    println("Es ist Tag")

    /** */
  }

  def GameEnd(): Game =
    val newIsRunning = false
    notifyObservers(GameEvent.gameEnd(newIsRunning))
    copy(isRunning = false)


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
