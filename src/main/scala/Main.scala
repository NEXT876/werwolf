package de.htwg.werwolf

import scala.io.StdIn.readLine



class Werwolf(name : String) extends Player(name){ 
  def vote(vote : Player) : Unit = println (s"Werwolf ${name} is voting for ${vote} to die")
  def role : String = "Werwolf"
 }

class Villager(name : String) extends Player(name) { 
    def vote(vote : Player) : Unit = println (s"Villager ${name} is voting for ${vote} to die")
    def role : String = "Villager"
 }

abstract class Player(name : String){
    protected var _isAlive : Boolean = true
 
    def isAlive : Boolean = _isAlive
    def die() : Unit =  _isAlive = false

    def vote(vote : Player) : Unit

    def role : String

    override def toString(): String = s"player: $name |  role : $role"
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

def IncrementRolesCount(rolesAmount : Map[String, Int], role : String) : Map[String,Int] =
  val currentCount = rolesAmount.getOrElse(role, 0)  
  rolesAmount + (role -> (currentCount + 1))


def addRole(player : Array[String]) : (Array[Player],Map[String,Int]) = {
  val playerWithRoles = new  Array[Player](player.size)
  var rolesAmount = Map.empty[String, Int]
  var count = 0

  if player.size == 2 then  
    playerWithRoles(count) = Werwolf(player(count)) 
    rolesAmount = IncrementRolesCount(rolesAmount, "werwolf")
    count += 1
    playerWithRoles(count) = Villager(player(count))
    rolesAmount = IncrementRolesCount(rolesAmount, "villager")
 
  (playerWithRoles,rolesAmount)
}

@main
  def main(): Unit = {
    val player = start()
    val (playerToRoles, rolesAmount) = addRole(player)
    playerToRoles.foreach { x => println(s"$x") } 
    //rolesAmount.foreach { (x,y) => println(s"$x : $y") }
  }

