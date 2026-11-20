// Datei: src/main/scala/de/htwg/werwolf/Main.scala
package de.htwg.werwolf

import de.htwg.werwolf.view.{GUI, TUI}
import de.htwg.werwolf.config.given

@main def Main(): Unit =

  // 2. TUI erzeugen und registrieren
  val tui = TUI()

  // 3. GUI initialisieren und registrieren
  GUI.init()

  // 4. TUI parallel starten
  // Daemon: Schließen der GUI beendet auch das Programm
  val tuiThread = new Thread(() => tui.start())
  tuiThread.setDaemon(true)
  tuiThread.start()

  // 5. GUI starten (blockiert den Main-Thread)
  GUI.main(Array())
