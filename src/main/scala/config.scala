package de.htwg.werwolf

import model.{NarratorInterface, GameCoreInterface, CommandInterface}
import controller.GameControllerInterface
import de.htwg.werwolf.model.narratorComponent.JsonNarrator
import de.htwg.werwolf.model.commandComponent.ExecuteC
import de.htwg.werwolf.model.gameCoreComponents.GameCore
import de.htwg.werwolf.controller.gameControllerComponent.GameController
import de.htwg.werwolf.model.Game


object config:  
  given NarratorInterface =
    new JsonNarrator(
      os.pwd / "src" / "main" / "resources" / "narrator.json"
    )
  given CommandInterface = new ExecuteC
  given GameCoreInterface = new GameCore
  given GameControllerInterface = new GameController(Game())


