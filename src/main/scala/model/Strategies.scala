package de.htwg.werwolf.model

trait NightActionStrategy {
  def performAction(player: Player, game: Game): Game
  def canAct(player: Player): Boolean = player.isAlive
}

case object WerwolfAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Game = {
    println(s"${player.name} (Werwolf) darf jetzt töten...")
    val possibleTargets = game.players.collect {
      case (name, p) if p.isAlive && name != player.name => name
    }.toVector
    if possibleTargets.isEmpty then game
    else
      val targetName = possibleTargets.head
      val cmd = KillCommand(player.name, targetName)
      game.executeCommand(cmd)
  }
}

case object WitchAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Game = {
    println(s"${player.name} (Hexe) darf heilen oder vergiften...")
    game
  }
}

case object TerroristAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Game = {
    println(s"${player.name} (Terorist) darf jetzt explodieren...")
    game
  }
}

case object AmorAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Game = {
    println(s"${player.name} (Amor) darf jetzt verlieben...")
    game
  }
}

case object VillagerAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Game = {
    println(s"${player.name} (Villager) darf jetzt leben...")
    game
  }
}

case object NoAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Game = {
    println(s"${player.name} hat heute Nacht nichts zu tun.")
    game
  }
}