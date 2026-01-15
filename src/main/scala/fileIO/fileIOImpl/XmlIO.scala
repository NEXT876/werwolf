package de.htwg.werwolf.fileIO.fileIOImpl

import scala.xml.*
import java.nio.file.Path
import de.htwg.werwolf.model.commandComponent.GameMemento
import de.htwg.werwolf.model.Phase
import de.htwg.werwolf.model.gameCoreComponents.*
import de.htwg.werwolf.fileIO.IOInterface

class XmlIO extends IOInterface:
  override val extension: String = ".xml"
  override def write(path: Path, data: GameMemento): Unit =
    XML.save(path.toString, gameToXml(data), "UTF-8", xmlDecl = true)

  override def read(path: Path): GameMemento =
    xmlToGame(XML.loadFile(path.toFile))

  private def gameToXml(game: GameMemento): Elem =
    <game>
      <day>{game.day}</day>
      <running>{game.isRunning}</running>
      <phase>{game.phase.toString}</phase>
      <players>
        { game.players.map { case (name, player) =>
            <player type={player.getClass.getSimpleName}>
              <name>{player.name}</name>
              <isAlive>{player.isAlive}</isAlive>
              <role>{player.role.toString}</role>
              <faction>{player.faction.toString}</faction>
            </player>
          }
        }
      </players>
      <votes>
        { game.votes.votes.map { case (player, count) =>
            <vote player={player}>{count}</vote>
          }
        }
      </votes>
    </game>

  private def xmlToGame(xml: Elem): GameMemento =
    val players: Map[String, Player] =
      (xml \ "players" \ "player").map { p =>
        val name = (p \ "name").text
        val role = (p \ "role").text
        val isAlive = (p \ "isAlive").text.toBoolean

        val player: Player = role match
          case "Werwolf" => Werwolf(name, isAlive)
          case "Villager" => Villager(name, isAlive)
          case "Witch" => Witch(name, isAlive)
          case "Amor" => Amor(name, isAlive)
          case "Terrorist" => Terrorist(name, isAlive)
          case _ => throw new RuntimeException(s"Unknown role: $role")

        name -> player
      }.toMap

    val day = (xml \ "day").text.toInt
    val isRunning = (xml \ "running").text.toBoolean

    val phase = (xml \ "phase").text match
      case "Day" => Phase.Day
      case "Night" => Phase.Night
      case other => throw new RuntimeException(s"Unknown phase: $other")

    val votesMap: Map[String, Int] =
      (xml \ "votes" \ "vote").map(v => (v \@ "player") -> v.text.toInt).toMap

    val votes = Votes(votesMap)

    GameMemento(players, phase, day, votes, isRunning, Vector.empty) // CommandHistory ignored
