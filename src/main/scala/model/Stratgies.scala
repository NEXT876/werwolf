package de.htwg.werwolf.model

trait NightActionStrategy {
  def performAction(player: Player, game: Game): Unit
  def canAct(player: Player): Boolean = player.isAlive
}

case object WerwolfAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Werwolf) darf jetzt töten...")
    // z. B. game.requestKillTarget(player)
  }
}

case object WitchAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Hexe) darf heilen oder vergiften...")
  }
}

case object TerroristAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Terorist) darf jetzt explodieren...")
    // z. B. game.requestKillTarget(player)
  }
}

case object AmorAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Werwolf) darf jetzt verlieben...")
    // z. B. game.requestKillTarget(player)
  }
}

case object VillagerAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Werwolf) darf jetzt leben...")
    // z. B. game.requestKillTarget(player)
  }
}

case object NoAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} hat heute Nacht nichts zu tun.")
  }
}