package de.htwg.werwolf.fileIO.fileIOImpl

import de.htwg.werwolf.fileIO.IOInterface
import de.htwg.werwolf.model.commandComponent.GameMemento
import play.api.libs.json._
import java.nio.file.{Files, Path}
import PlayerJson._
import GameMementoJson._

class JsonIO extends IOInterface:
  override val extension: String = ".json"
  override def write(path: Path, data: GameMemento): Unit =
    val jsonStr = Json.prettyPrint(Json.toJson(data))
    Files.writeString(path, jsonStr)

  override def read(path: Path): GameMemento =
    val jsonStr = Files.readString(path)
    val result: JsResult[GameMemento] = Json.parse(jsonStr).validate[GameMemento]

    Json.parse(jsonStr).validate[GameMemento] match
      case JsSuccess(game, _) => game
      case JsError(errors) => throw new RuntimeException(s"JSON parsing failed: $errors")
