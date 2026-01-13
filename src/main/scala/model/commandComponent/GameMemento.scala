// src/main/scala/de/htwg/werwolf/model/commandComponent/GameMemento
package de.htwg.werwolf.model.commandComponent

import de.htwg.werwolf.model.gameCoreComponents.{Player, Votes}
import de.htwg.werwolf.model.Phase


case class GameMemento(
    players: Map[String, Player],
    phase: Phase,
    day: Int,
    votes: Votes,
    isRunning: Boolean,
    commandHistory: Vector[GameCommand]
)
