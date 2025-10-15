import scala.io.StdIn.readLine
import scala.io.Source
import java.io.File

@main def start(): Unit =
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
    
  val Spieleranzahl = readLine("Bitte Spieleranzahl eingeben ( Mindestanzahl 2): ")
  



