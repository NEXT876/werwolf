package de.htwg.werwolf.model

trait GameCommand {
  def execute(): Unit
  def undo(): Unit
  def description: String  // für Logging/Replay
}


case class KillCommand(killer: Player, target: Player, game: Game) extends GameCommand {
  private val oldTarget: Player = target  // neue Instanz nach execute

  override def execute(): Unit = {
    if target.isAlive then target.die 
  }

  override def undo(): Unit = {
    // undo: alte Instanz wieder herstellen
    if !(oldTarget.isAlive) then oldTarget.revive
  }

  override def description: String = s"${killer.name} tötet ${target.name}"
}

case class HealCommand(witch: Player, target: Player) extends GameCommand {
  private var oldTarget: Player = target
  private var newTarget: Player = target

  override def execute(): Unit = {
    newTarget = target match
      case p if !p.isAlive => p match
        case wp: Werwolf => wp.copy(isAlive = true)  // Bei allen konkreten Playern copy benutzen
        case _ => target  // Andere Spielerarten
      case _ => target
    oldTarget = target
  }

  override def undo(): Unit = {
    newTarget = oldTarget
  }

  override def description: String = s"Hexe heilt ${target.name}"
}

