// src/main/scala/de/htwg/werwolf/model/GameEvents.scala
package de.htwg.werwolf.model

enum GameEvent:
  case printGameState(players : Map[String, Player])
  case phaseSwitch(phase: Phase)
  case gameEnd(isRunning: Boolean)
