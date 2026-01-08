package de.htwg.werwolf.model.roleUtils
import scala.util.Random
import de.htwg.werwolf.model.*

class PlayerInitializer extends PlayerInterface {
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
}