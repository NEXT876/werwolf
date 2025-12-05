package de.htwg.werwolf.model

trait GameCommand {
  def description: String
  def execute(game: Game): Game
  def undo( game: Game): Game
}


case class KillCommand(killerName: String, targetName: String) extends GameCommand {

  override def description: String = s"${killerName} tötet ${targetName}"
  override def execute(game: Game): Game = {
    game.players.get(targetName) match {
      case Some(target) if target.isAlive =>
        val killedPlayer = target.die
        game.copy(players = game.players.updated(targetName, killedPlayer))
      case _ => game // Ziel schon tot oder nicht existent → nichts tun
    }
  }

  override def undo(game: Game): Game = {
    game.players.get(targetName) match {
      case Some(target) if !target.isAlive =>
        val revivedPlayer = target.revive
        game.copy(players = game.players.updated(targetName, revivedPlayer))
      case _ => game
    }
  }
}

case class GameEndCommand(winner: Option[Faction] = None) extends GameCommand {

  override val description: String = winner match
      case Some(w) => s"Spiel beendet – Gewinner: $w"
      case None    => "Spiel beendet (manuell abgebrochen)"


  override def execute(game: Game): Game = {
    game.copy(isRunning = false)
  }

  override def undo(game: Game): Game = {
    game.copy(isRunning = true)
  }
}