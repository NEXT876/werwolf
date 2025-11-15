// src/main/scala/de/htwg/werwolf/model/GameEvents.scala
package de.htwg.werwolf.model

enum GameEvent:

  case NightPhaseStarted(roles: Map[String, Player])
  case WerewolfTurn(name: String, roles: Map[String, Player])
  case GameStart(roles: Map[String, Player])
  case RequestPlayerName(index: Int)
  case DuplicateNameWarning
