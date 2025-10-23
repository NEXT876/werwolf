package de.htwg.werwolf


def start(): Vector[String] = {
    import scala.io.StdIn.readLine
    println("Willkommen zu Werwolf")
    val Spieleranzahl = readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2): ").toInt

    val names: Vector[String] = (0 until Spieleranzahl)
    .map(i => readLine(s"Spieler ${i+1}: Wie heißen sie: "))
    .toVector

    names
}


@main
  def main(): Unit = {
    val player = start()
    print(player);
  }

