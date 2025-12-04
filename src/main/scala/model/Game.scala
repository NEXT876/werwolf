// src/main/model/Game.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.narrator.*

import upickle.default.*
import scala.util.Random
import scala.collection.mutable.Stack
import de.htwg.werwolf.util.Subject

enum Phase:
  case Night, Day

enum Faction:
  case _Werwolf, _Villager
  def winCondition(players: Map[String, Player]): Boolean = this match
    case Faction._Werwolf =>
      players.values.forall(p => p.faction == Faction._Werwolf)
    case Faction._Villager =>
      players.values.forall(p => p.faction != Faction._Werwolf)

enum Roles:
  case werwolf, villager, terrorist, witch, amor
  def toPlayer(name: String): Player = this match
    case Roles.werwolf   => Werwolf(name)
    case Roles.villager  => Villager(name)
    case Roles.terrorist => Terrorist(name)
    case Roles.witch     => Witch(name)
    case Roles.amor      => Amor(name)

case class GameMemento(
    players: Map[String, Player],
    phase: Phase,
    day: Int,
    votes: Votes,
    isRunning: Boolean,
    commandHistory: Stack[GameCommand]
)

case class Game (
    players: Map[String, Player] = Map.empty,
    phase: Phase = Phase.Night,
    day: Int = 1,
    votes: Votes = Votes(),
    isRunning: Boolean = true,
    commandHistory: Stack[GameCommand] = Stack[GameCommand]()
) extends Subject[GameEvent] {

  private val narratorData: Root = NarratorService.loadNarratorJson(
    os.pwd  / "src" / "main"/ "resources" / "narrator.json"
  )

  def executeCommand(cmd: GameCommand): Game = {
    val updatedGame = cmd.execute(this)
    updatedGame.copy(commandHistory = commandHistory.push(cmd))
  }

  def undoLast(): Game = 
    if (commandHistory.nonEmpty) {
      val cmd = commandHistory.pop()
      val revertedGame = cmd.undo(this)
      revertedGame.copy(commandHistory = commandHistory)
    } else {
      println("Nichts zum Rückgängigmachen!")
      this
    }

  def replay(): Unit =
    println("=== REPLAY ===")
    commandHistory.reverse.foreach { cmd =>
      println(s"• ${cmd.description}")
    }
  

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
                      //
                      //

  def addRoles(playerNames: Vector[String]): Game =
    val roles = getRoles(playerNames.size)

    val newPlayers = Random
      .shuffle(playerNames)
      .zip(roles)
      .map { case (name, role) =>
        val player = role.toPlayer(name)
        player.name -> player
      }
      .toMap
    copy(players = newPlayers)


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


  def runNightPhase(): Unit = {
    notifyObservers(GameEvent.printnarratorText(NarratorService.randomNarratorText("Start", narratorData)))
    notifyObservers(GameEvent.printGameState(players))
    players.foreach { (name, player) => player.nightAction.performAction(player, this)}
    /** */
  }

  def runDayPhase(): Unit = {
    notifyObservers(GameEvent.printGameState(players))
    /** */
  }

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