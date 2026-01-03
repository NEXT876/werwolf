// src/main/scala/model/Roles.scala

package de.htwg.werwolf.model

trait Player:
  def name: String
  def isAlive: Boolean
  def role: Roles
  def vote(target: Player): String
  def die: Player
  def revive: Player
  def faction: Faction
  def nightAction: NightActionStrategy

  override def toString(): String =
    f"• ${name}%-15s | Rolle: ${role}%-10s | Status: ${if (isAlive) "lebt" else "tot"}%-7s\n"

abstract class PlayerDecorator(inner: Player) extends Player:
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
  protected def copyWith(newPlayer: Player): Player


final case class Werwolf(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.werwolf
  def faction = Faction._Werwolf
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = WerwolfAction

final case class Villager(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.villager
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = VillagerAction

final case class Amor(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.amor
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = AmorAction

final case class Terrorist(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.terrorist
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = TerroristAction

final case class Witch(name: String, isAlive: Boolean = true) extends Player:
  def role = Roles.witch
  def faction = Faction._Villager
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def revive = copy(isAlive = true)
  def nightAction: NightActionStrategy = WitchAction

final case class DoubleVoteDecorator(inner: Player)
  extends PlayerDecorator(inner):

  override def vote(target: Player): String =
    s"${inner.name} votes TWICE for ${target.name}!"

  override protected def copyWith(newPlayer: Player): Player =
    DoubleVoteDecorator(newPlayer)
