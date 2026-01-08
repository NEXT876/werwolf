package de.htwg.werwolf.model.phaseComponent

import de.htwg.werwolf.model.Game
import de.htwg.werwolf.model.commandComponent.CommandInterface

trait PhaseComponentInterface:
    def switchPhase(game : Game): Game
    def runNightPhase(game : Game)(using ci: CommandInterface): Game
    def runDayPhase(game : Game)(using ci: CommandInterface): Game