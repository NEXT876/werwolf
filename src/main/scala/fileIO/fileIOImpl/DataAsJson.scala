package de.htwg.werwolf.fileIO.fileIOImpl

import play.api.libs.json._
import de.htwg.werwolf.model.gameCoreComponents.*
import de.htwg.werwolf.model.commandComponent.GameMemento
import de.htwg.werwolf.model.Phase
import de.htwg.werwolf.fileIO.fileIOImpl.PhaseJson.phaseFormat
import de.htwg.werwolf.fileIO.fileIOImpl.VotesJson.votesFormat

object PlayerJson:
  implicit val werwolfFormat: OFormat[Werwolf] = Json.format[Werwolf]
  implicit val villagerFormat: OFormat[Villager] = Json.format[Villager]
  implicit val witchFormat: OFormat[Witch] = Json.format[Witch]
  implicit val amorFormat: OFormat[Amor] = Json.format[Amor]
  implicit val terroristFormat: OFormat[Terrorist] = Json.format[Terrorist]

  implicit val playerWrites: Writes[Player] = Writes {
    case w: Werwolf => Json.toJson(w)
    case v: Villager => Json.toJson(v)
    case w: Witch => Json.toJson(w)
    case a: Amor => Json.toJson(a)
    case t: Terrorist => Json.toJson(t)
    case d: PlayerDecorator => Json.toJson(d.decorated)
  }

  implicit val playerReads: Reads[Player] = Reads { js =>
    (js \ "role").asOpt[String] match
      case Some("werwolf") => js.validate[Werwolf]
      case Some("villager") => js.validate[Villager]
      case Some("witch") => js.validate[Witch]
      case Some("amor") => js.validate[Amor]
      case Some("terrorist") => js.validate[Terrorist]
      case _ => JsError("Unknown Player type")
  }

  // generisches Map-Format für Player
  implicit val playerMapFormat: Format[Map[String, Player]] =
    Format(
      Reads(js => js.validate[Map[String, JsValue]].flatMap { m =>
        val mapped = m.map { case (k, v) => k -> v.validate[Player] }
        val errors = mapped.collect { case (k, JsError(e)) => k -> e }
        if errors.nonEmpty then JsError(errors.head._2)
        else JsSuccess(mapped.collect { case (k, JsSuccess(p, _)) => k -> p }.toMap)
      }),
      Writes(m => Json.toJson(m.view.mapValues(p => Json.toJson(p)).toMap))
    )

object GameMementoJson:
  import PlayerJson._

  implicit val gameMementoFormat: OFormat[GameMemento] =
    new OFormat[GameMemento]:
      override def writes(gm: GameMemento): JsObject =
        Json.obj(
          "players" -> Json.toJson(gm.players),
          "phase" -> Json.toJson(gm.phase),
          "day" -> JsNumber(gm.day),
          "votes" -> Json.toJson(gm.votes),
          "isRunning" -> JsBoolean(gm.isRunning)
        )

      override def reads(json: JsValue): JsResult[GameMemento] =
        for
          players <- (json \ "players").validate[Map[String, Player]]
          phase <- (json \ "phase").validate[Phase]
          day <- (json \ "day").validate[Int]
          votes <- (json \ "votes").validate[Votes]
          isRunning <- (json \ "isRunning").validate[Boolean]
        yield GameMemento(players, phase, day, votes, isRunning)

object PhaseJson:
  implicit val phaseFormat: Format[Phase] = Format(
    Reads(js => js.validate[String].map {
      case "Day" => Phase.Day
      case "Night" => Phase.Night
      case other => throw new RuntimeException(s"Unknown Phase: $other")
    }),
    Writes(phase => JsString(phase.toString))
  )

object VotesJson:
  implicit val votesFormat: OFormat[Votes] = Json.format[Votes]
