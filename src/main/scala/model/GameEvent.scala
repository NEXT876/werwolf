// src/main/scala/de/htwg/werwolf/model/GameEvents.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.model.gameCoreComponents.Player

enum GameEvent:
  case printGameState(players : String)
  case printnarratorText(text : String)
  case printText(text : String, waitTime : Int)
  case clearScreen
  case showLogo
  case requestPlayerNames
  case GameOver
  case printErrorMSG(msg : String)
  case switchPhase(phase : String)
  case InitialthingsDone
  case askForTarget(name : String, role : Roles, targets : Vector[String])