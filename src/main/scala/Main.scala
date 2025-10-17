package de.htwg.werwolf

import scala.io.StdIn.readLine
import scala.compiletime.ops.boolean


class werwolf(name : String) extends player(name){ 
  def vote(vote : player) : Unit = println (s"Werwolf ${name} is voting for ${vote} to die")
  def role : String = "Werwolf"
 }

class villager(name : String) extends player(name) { 
    def vote(vote : player) : Unit = println (s"Villager ${name} is voting for ${vote} to die")
    def role : String = "Villager"
 }

abstract class player(name : String){
    protected var _isAlive : Boolean = true
 
    def isAlive : Boolean = _isAlive
    def die() : Unit =  _isAlive = false

    def vote(vote : player) : Unit

    def role : String
  }

def start(): Array[String] = {
  import scala.io.Source
  import java.io.File
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
  
  names
}


object Main {
  def main(args: Array[String]): Unit = {
    val player = start()
    //addRole(player)
  }
}
