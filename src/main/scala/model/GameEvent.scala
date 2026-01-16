// src/main/scala/de/htwg/werwolf/model/GameEvents.scala
package de.htwg.werwolf.model

import de.htwg.werwolf.model.gameCoreComponents.Player

enum GameEvent:
  case printGameState(players : String)
  case printNarratorText(text : String)
  case printText(text : String, waitTime : Int)
  case clearScreen
  case showLogo
  case requestPlayerNames
  case gameOver
  case printErrorMSG(msg : String)
  case switchPhase(phase : String)
  case initialThingsDone
  case askForTargetNight(name : String, role : Roles, targets : Vector[String])
  case askForTargetDay(name : String, targets : Vector[String])
  case gameLoaded()