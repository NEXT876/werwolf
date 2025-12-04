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
