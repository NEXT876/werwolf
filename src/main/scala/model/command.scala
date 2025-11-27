package de.htwg.werwolf.model

trait GameCommand {
  def execute(): Unit
  def undo(): Unit
  def description: String  // für Logging/Replay
}


case class KillCommand(killer: Player, target: Player, game: Game) extends GameCommand {
  private val wasAlive = target.isAlive

  def execute(): Unit = {
    /* ToDo */
  }

  def undo(): Unit = {
    /* ToDo */
  }

  def description = s"${killer.name} tötet ${target.name}"
}


case class HealCommand(witch: Witch, target: Player) extends GameCommand {
  private val wasDead = !target.isAlive

  def execute(): Unit = {
   /* ToDo */
  }

  def undo(): Unit = {
    /* ToDo */
  }

  def description = s"Hexe heilt ${target.name}"
}