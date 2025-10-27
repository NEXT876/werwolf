package de.htwg.werwolf

enum Roles:
    case werwolf
    case villager
    case amor
    case terrorist
    case witch

    def toPlayer (name : String) : Player = this match
        case Roles.werwolf  => Werwolf(name)
        case Roles.villager => Villager(name)
        case Roles.amor => Amor(name)
        case Roles.terrorist => Terrorist(name)
        case Roles.witch => Witch(name)
    
def addRoles(players : Vector[String]) : Map[String, Player] = {
    import scala.util.Random
    val playerRoles : Map[String, Player] = Map()

    if players.size == 2 then  // sorgt dafür dass wenn es nur 2 Spieler gibt es immer 1 Werwolf und 1 Villager sind(sonst unnötig)
        val roles = Vector(Roles.werwolf, Roles.villager)
        val shuffeledRoles = Random.shuffle(roles)
        (players zip shuffeledRoles)
        .map { case (name, role) =>
            val player = role.toPlayer(name)
            player.name -> player
        }.toMap

    else
        val werwolfAmount = if players.size <= 3 then 1 else 2
        val roles = Vector(Roles.villager, Roles.witch, Roles.amor, Roles.terrorist)
        val shuffeledRoles = Random.shuffle(roles)
        val finalRoles : Vector[Roles] = (Vector.fill(werwolfAmount)(Roles.werwolf)
        ++ shuffeledRoles).take(players.size)  
        (players zip finalRoles)
        .map {case (name, role) =>
            val player = role.toPlayer(name)
            player.name -> player
        }.toMap
}
