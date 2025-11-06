package de.htwg.werwolf

import scala.io.StdIn.readLine
import java.util.concurrent.atomic.AtomicInteger

object GlobalDayCounter{
  private val day = new AtomicInteger(0)

  def increment() : Int = day.incrementAndGet()
  def get() : Int = day.get()
  def reset() : Unit = day.set(0)
}

/*
  "\u001b[31m", // Rot
  "\u001b[32m", // Grün
  "\u001b[33m", // Gelb
  "\u001b[34m", // Blau
  "\u001b[35m", // Magenta
  "\u001b[36m", // Cyan
  "\u001b[1m",  // Fett
  "\u001b[4m"   // Unterstrichen
)
reset = "\u001b[0m"
*/

def getplayerAmount(test : Boolean, fakeInput : Int): Int = {
    val playerAmount = 
      if test then
       fakeInput
      else
        readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2, Maximale Spieleranzahl 7): ").toInt
    playerAmount
}

def getPlayerNames(test : Boolean, playerAmount : Int, fakeInput : Vector[String]): Vector[String] = {
      if test then
      fakeInput
      else
        (0 until playerAmount)
        .foldLeft(Set[String]()) { (acc, _) =>
          var name = readLine("Name").trim
           while(acc.contains(name)){
            println("name gibt es bereits")
            name = readLine("Name").trim
          } 

          acc + name
        }.toVector
}

def tiping(text : String, waitTime_ms : Int) :Boolean = {
  text.foreach { buchstabe =>
    if(buchstabe == '.'){
      Thread.sleep(waitTime_ms*4)
      print(buchstabe)
    }else{
      print(buchstabe)
      Thread.sleep(waitTime_ms)
    }
  }
  println()
  return true
}

@main
  def main(): Unit = {
    tiping("Willkommen zu Werwolf", 100)
    val playerAmount = getplayerAmount(false, 0)
    if playerAmount < 2 || playerAmount > 6 then
      println("\u001b[31mUngültige Spieleranzahl\u001b[0m");
      return;
    val player = getPlayerNames(false, playerAmount, Vector[String]())
    val playerRoles= addRoles(player)
    println(printPlayerRoles(playerRoles))

    val updatedplayerroles = night(playerRoles)
    println(printPlayerRoles(updatedplayerroles))
  }

