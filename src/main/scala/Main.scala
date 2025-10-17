package de.htwg.werwolf

import scala.io.StdIn.readLine
import scala.io.Source
import java.io.File

//@main
def start(): Unit =
  println("Willkommen zu Werwolf")

  val input = readLine("Spielanleitung anzeigen? ( Yes/No)").toLowerCase()
  val spieleanleitung_anzeigen = if input == "yes" then true else false

  if spieleanleitung_anzeigen then
    val filePath = "src/resources/werwolf_spielanleitung.txt"
    try{
      val content = Source.fromFile(filePath).mkString
      println(content)
      Source.fromFile(filePath).close()
    } catch {
      case e: Exception => println(s"fehler beim lesen der Datei: ${e.getMessage}")
    }
    
  val Spieleranzahl = readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2): ").toInt

  val names = new Array[String](Spieleranzahl)

  for i <- 0 until Spieleranzahl do
    names(i) = readLine(s"Spieler ${i+1}: Wie heißen sie: ")
  
  println("Das Spiel beginnt mit folgenden Spielern:")
  names.zipWithIndex.foreach{ case (name, index) => println(s"Spieler${index+1}: $name")}

  readLine("Um das Spiel zu beginnen, bitte eine beliebige Taste drücken")
