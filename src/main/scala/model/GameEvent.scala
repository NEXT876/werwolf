// src/main/scala/de/htwg/werwolf/model/GameEvents.scala
package de.htwg.werwolf.model

sealed trait GameEvent

case class NightPhaseStarted(roles: Map[String, Player]) extends GameEvent
case class WerewolfTurn(name: String, roles: Map[String, Player]) extends GameEvent