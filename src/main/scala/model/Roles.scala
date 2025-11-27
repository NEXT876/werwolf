// src/main/scala/model/Roles.scala

// Prototyp pattern möglich
// aber nicht sinnvoll
// da wenig overhead

// factory pattern zählt nicht
// aber haben wir ja schon sowieso in game

// builder pattern möglich
// aber auch unnötig
// macht rollen erstellung länger
// entweder builder oder prototyp

// command pattern sehr sinvoll
// gut mit memento pattern kombinierbar

// Strategie pattern auch sehr sinvoll
// sehr leicht zu implementieren
// seehr gut mit command pattern kombinierbar

package de.htwg.werwolf.model

trait Player:
  def name: String
  def isAlive: Boolean
  def role: String
  def vote(target: Player): String
  def die: Player
  def nightAction: NightActionStrategy

final case class Werwolf(name: String, isAlive: Boolean = true) extends Player:
  def role = "Werwolf"
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def nightAction: NightActionStrategy = WerwolfAction

final case class Villager(name: String, isAlive: Boolean = true) extends Player:
  def role = "Villager"
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def nightAction: NightActionStrategy = VillagerAction

final case class Amor(name: String, isAlive: Boolean = true) extends Player:
  def role = "Amor"
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def nightAction: NightActionStrategy = AmorAction

final case class Terrorist(name: String, isAlive: Boolean = true) extends Player:
  def role = "Terrorist"
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def nightAction: NightActionStrategy = TerroristAction

final case class Witch(name: String, isAlive: Boolean = true) extends Player:
  def role = "Witch"
  def vote(target: Player) = s"${role} $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
  def nightAction: NightActionStrategy = WitchAction
