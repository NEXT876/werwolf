package de.htwg.werwolf

 import scala.io.StdIn.readLine

def getplayerAmount(test : Boolean, fakeInput : Int): Int = {
    println("Willkommen zu Werwolf")
    val playerAmount = 
      if test then
       fakeInput
      else
        readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2, Maximale Spieleranzahl 7): ").toInt
    playerAmount
}

def getPlayerNames(test : Boolean, playerAmount : Int, fakeInput : Vector[String]): Vector[String] = {
    val names =
      if test then
      fakeInput
      else
        (0 until playerAmount)
        .map(i => readLine(s"Spieler ${i+1}: Wie heißen sie: "))
        .toVector
    names
}

@main
  def main(): Unit = {
    val playerAmount = getplayerAmount(false, 0)
    if playerAmount < 2 || playerAmount > 6 then
      println("\u001b[31mUngültige Spieleranzahl\u001b[0m");
      return;
    val player = getPlayerNames(false, playerAmount, Vector[String]())
    val playerRoles= addRoles(player)
    println(printPlayerRoles(playerRoles))
  }

