// Datei: src/main/scala/de/htwg/werwolf/Main.scala
package de.htwg.werwolf

import controller.GameController
import model.Game
import de.htwg.werwolf.view.*
import de.htwg.werwolf.util.*

@main def Main(args: String*): Unit =

      val guiThread = new Thread(() => {
        GUI.main(Array())
      })
      guiThread.start()
      
      // 2. Kurz warten, bis GUI initialisiert ist
      Thread.sleep(2000) 
      
      val controller = GameController(Game())
      
      val tui = TUI(controller)
      controller.addObserver(tui)
      controller.addObserver(GUI)
      tui.start()  
  