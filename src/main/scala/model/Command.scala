package de.htwg.werwolf.model

trait GameCommand {
  def execute(): Unit
  def undo(): Unit
  def description: String  // für Logging/Replay
}


case class KillCommand(killer: Player, target: Player, game: Game) extends GameCommand {
  private var oldTarget: Player = target  // merken für undo
  private var newTarget: Player = target  // neue Instanz nach execute

  override def execute(): Unit = {
    newTarget = target.die
    // Optional: das Game updaten, z.B. Spieler in der Map ersetzen
    oldTarget = target
  }

  override def undo(): Unit = {
    // undo: alte Instanz wieder herstellen
    newTarget = oldTarget
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

