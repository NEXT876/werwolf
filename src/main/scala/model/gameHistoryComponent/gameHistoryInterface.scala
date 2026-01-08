// src/main/scala/de/htwg/werwolf/gameHistory/gameHistoryInterface.scala
package de.htwg.werwolf.model.gameHistoryComponent

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.commandComponent.CommandInterface


trait gameHistoryInterface:
    def save(game: Game)(using ci : CommandInterface): Unit
    def undo(game: Game)(using ci : CommandInterface): Unit
    def list(): Unit























