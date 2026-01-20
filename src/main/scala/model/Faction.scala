package de.htwg.werwolf.model

import de.htwg.werwolf.model.gameCoreComponents.Player

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
