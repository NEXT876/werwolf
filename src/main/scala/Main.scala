package de.htwg.werwolf

 import scala.io.StdIn.readLine

def getplayerAmount(): Int = {
    println("Willkommen zu Werwolf")
    val playerAmount = readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2, Maximale Spieleranzahl 7): ").toInt
    playerAmount
}

def getPlayerNames(playerAmount : Int): Vector[String] = {
    val names = (0 until playerAmount)
    .map(i => readLine(s"Spieler ${i+1}: Wie heißen sie: "))
    .toVector
    names
}

@main
  def main(): Unit = {
    val playerAmount = getplayerAmount()
    if playerAmount < 2 || playerAmount > 6 then 
      println("\u001b[31mUngültige Spieleranzahl\u001b[0m"); 
      return;
    val player = getPlayerNames(playerAmount)
    val playerRoles= addRoles(player)
    playerRoles.foreach((role, player) => println(s"${playerRoles(role)}"))
  }

