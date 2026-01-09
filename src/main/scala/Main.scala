// Datei: src/main/scala/de/htwg/werwolf/Main.scala
package de.htwg.werwolf

import de.htwg.werwolf.view.{GUI, TUI}
import de.htwg.werwolf.util.Subject
import de.htwg.werwolf.controller.gameControllerComponent.{GameController,GameControllerInterface}
import de.htwg.werwolf.model.Game
import model.commandComponent.{CommandInterface, ExecuteC}
import model.narratorComponent.{NarratorInterface, JsonNarrator}
import model.gameCoreComponents.{GameCoreInterface, GameCore}

@main def Main(): Unit =

  given NarratorInterface =
    new JsonNarrator(
      os.pwd / "src" / "main" / "resources" / "narrator.json"
    )
  given CommandInterface = new ExecuteC
  given GameCoreInterface = new GameCore
  
  
  // 1. Controller erzeugen (konkrete Instanz!)
  val controller: GameControllerInterface = GameController(Game())

  // 2. TUI erzeugen und registrieren
  val tui = TUI(controller)
  controller.addObserver(tui)

  // 3. GUI initialisieren und registrieren
  GUI.init(controller)
  controller.addObserver(GUI)


  // 4. TUI parallel starten
  new Thread(() => tui.start()).start()

  // 5. GUI starten (blockiert den Main-Thread)
  GUI.main(Array())
