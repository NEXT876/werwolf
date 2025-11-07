package de.htwg.werwolf

def printPlayerRoles(playerRoles: Map[String, Player]): String = {
  val header = "\n================ Spieler & Rollen ================\n"
  val body = playerRoles
    .map { case (name, player) =>
      val role = player.role
      val state = player.isAlive
      f"• $name%-15s | Rolle: $role%-10s | Status: ${if player.isAlive then "lebt" else "tot"}%-7s"
    }
    .mkString("\n")
  val footer = "\n==================================================\n"
  header + body + footer
}
