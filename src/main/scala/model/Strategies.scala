package de.htwg.werwolf.model

trait NightActionStrategy {
  def performAction(player: Player, game: Game): Unit
  def canAct(player: Player): Boolean = player.isAlive
}

case object WerwolfAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Werwolf) darf jetzt töten...")
    val target = scala.io.StdIn.readLine("Werwolf wenn möchtest du töten")
    val command = KillCommand(player.name, target)
    game.executeCommand(command)
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
  }
}

case object AmorAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Werwolf) darf jetzt verlieben...")
  }
}

case object VillagerAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} (Werwolf) darf jetzt leben...")
  }
}

case object NoAction extends NightActionStrategy {
  def performAction(player: Player, game: Game): Unit = {
    println(s"${player.name} hat heute Nacht nichts zu tun.")
  }
}