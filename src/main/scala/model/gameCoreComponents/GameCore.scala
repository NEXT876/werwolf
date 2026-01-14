package de.htwg.werwolf.model.gameCoreComponents

import de.htwg.werwolf.model.{GameCoreInterface, CommandInterface}
import de.htwg.werwolf.model.{Roles, Phase, Game}

import scala.util.Random
import de.htwg.werwolf.model.Faction

case class Votes(votes: Map[String, Int] = Map.empty):
  def addVote(player: String, game: Game): Votes =
    val current = votes.getOrElse(player, 0)
    copy(votes = votes.updated(player, current + 1))

  def getVotedPlayer(game: Game): Option[String] =
    if votes.isEmpty then None
    else Some(votes.maxBy(_._2)._1)

case class GameCore()(using ci: CommandInterface) extends GameCoreInterface {
  def switchPhase(game: Game): Game =
    ci.createMemento(game)
    val newPhase = if game.phase == Phase.Night then Phase.Day else Phase.Night
    val newDay = game.day + 1
    game.copy(phase = newPhase, day = newDay, votes = Votes())

  
  def addRoles(playerNames: Vector[String], game: Game): Game =
    val roles = getRoles(playerNames.size)

    val basePlayers: Map[String, Player] = Random
      .shuffle(playerNames)
      .zip(roles)
      .map { case (name, role) =>
        val player = role.toPlayer(name)
        name -> player
      }
      .toMap

    // decorater
    val updatedPlayers: Map[String, Player] = basePlayers
      .collectFirst {
        case (name, p) if !p.isInstanceOf[Werwolf] =>
          name -> DoubleVoteDecorator(p)
      }
      .fold(basePlayers) { case (name, decoratedPlayer) =>
        basePlayers.updated(name, decoratedPlayer)
      }

    game.copy(players = updatedPlayers)

  def getRoles(playeramount: Int): Vector[Roles] =
    if playeramount == 2 then Vector(Roles.werwolf, Roles.villager)
    else
      Vector.fill(playeramount / 3)(Roles.werwolf) ++ Random.shuffle(
        Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
      )

  def addVote(player: String, game: Game): Votes =
    game.votes.addVote(player, game)

  def getVotedPlayer(game: Game): Option[String] =
    game.votes.getVotedPlayer(game)

  def resetVotes() : Votes =
    Votes()

}
