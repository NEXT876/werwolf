package de.htwg.werwolf.model

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.CommandInterface
import de.htwg.werwolf.model.gameCoreComponents.Votes
import de.htwg.werwolf.model.gameCoreComponents.Player
import de.htwg.werwolf.model.gameCoreComponents.Werwolf
import de.htwg.werwolf.model.gameCoreComponents.Villager
import de.htwg.werwolf.model.gameCoreComponents.Terrorist
import de.htwg.werwolf.model.gameCoreComponents.Witch
import de.htwg.werwolf.model.gameCoreComponents.Amor



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


enum Phase:
    case Day,Night

trait GameCoreInterface:
    def switchPhase(game : Game): Game
    def runNightPhase(game : Game)(using ci: CommandInterface): Game
    def runDayPhase(game : Game)(using ci: CommandInterface): Game
    def addRoles(playerNames: Vector[String], game: Game): Game
    def getRoles(playeramount: Int): Vector[Roles]
    def addVote(player: String, game : Game): Votes
    def getVotedPlayer(game : Game): Option[String]


trait VotesInterface:
    def addVote(player: String, game : Game): Votes
    def getVotedPlayer(game : Game): Option[String]