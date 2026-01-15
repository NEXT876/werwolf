// de.htwg.werwolf.model.roleUtils.PlayerInitializer.scala
package de.htwg.werwolf.model.gameCoreComponents

import de.htwg.werwolf.model.strategiesComponent.{AmorAction, TerroristAction, VillagerAction, WerwolfAction, WitchAction, voteAction}
import de.htwg.werwolf.model.{NightActionStrategy, Roles, Faction}
import de.htwg.werwolf.model.narratorComponent.Night



trait Player:
  def name: String
  def isAlive: Boolean
  def role: Roles
  def vote(target: Player): String
  def die: Player
  def revive: Player
  def faction: Faction
  def nightAction: NightActionStrategy
  def dayAction: NightActionStrategy

  override def toString(): String =
    f"• ${name}%-15s | Rolle: ${role}%-10s | Status: ${if (isAlive) "lebt" else "tot"}%-7s"

abstract class PlayerDecorator(protected val inner: Player) extends Player:
  // for filesave
  def decorated: Player = inner
  def name: String = inner.name
  def isAlive: Boolean = inner.isAlive
  def role: Roles = inner.role
  def vote(target: Player): String = inner.vote(target)
  def die: Player =
    val died = inner.die
    this.copyWith(died)

  def revive: Player =
    val revived = inner.revive
    this.copyWith(revived)

  def faction: Faction = inner.faction
  def nightAction: NightActionStrategy = inner.nightAction
  def dayAction: NightActionStrategy = inner.dayAction
  
  protected def copyWith(newPlayer: Player): Player


final case class Werwolf(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.werwolf
  def faction = Faction._Werwolf
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = WerwolfAction
  def dayAction: NightActionStrategy = voteAction

final case class Villager(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.villager
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = VillagerAction
  def dayAction: NightActionStrategy = voteAction

final case class Amor(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.amor
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = AmorAction
  def dayAction: NightActionStrategy = voteAction

final case class Terrorist(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.terrorist
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = TerroristAction
  def dayAction: NightActionStrategy = voteAction

final case class Witch(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.witch
  def faction = Faction._Werwolf
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = WitchAction
  def dayAction: NightActionStrategy = voteAction

final case class DoubleVoteDecorator(override val inner: Player)
  extends PlayerDecorator(inner):
  override def decorated: Player = inner

  override def vote(target: Player): String =
    s"${inner.name} votes TWICE for ${target.name}!"

  override protected def copyWith(newPlayer: Player): Player =
    DoubleVoteDecorator(newPlayer)
