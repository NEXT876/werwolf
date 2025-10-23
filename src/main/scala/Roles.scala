package de.htwg.werwolf

sealed trait Player {
  def name: String
  def isAlive: Boolean
  def role: String
  def vote(target: Player): String
  def die: Player
}

final case class Werwolf(name: String, isAlive: Boolean = true) extends Player {
  def role = "Werwolf"
  def vote(target: Player) = s"Werwolf $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
}

final case class Villager(name: String, isAlive: Boolean = true) extends Player {
  def role = "Villager"
  def vote(target: Player) = s"Villager $name votes for ${target.name} to die"
  def die = copy(isAlive = false)
}
