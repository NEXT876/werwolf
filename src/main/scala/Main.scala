// Datei: src/main/scala/de/htwg/werwolf/Main.scala
package de.htwg.werwolf

import controller.GameController
import model.Game
import de.htwg.werwolf.view.*
import de.htwg.werwolf.util.*
@main def Main(): Unit =

  // 1. Controller erzeugen
  val controller = GameController(Game())

  // 2. TUI erzeugen und als Observer registrieren
  val tui = TUI(controller)
  controller.addObserver(tui)

  // 3. GUI initialisieren und als Observer registrieren
  GUI.init(controller)
  controller.addObserver(GUI)

  // 4. TUI parallel starten
  new Thread(() => tui.start()).start()

  // 5. GUI starten (blockiert den Main-Thread)
  GUI.main(Array())
