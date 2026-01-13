package de.htwg.werwolf.fileIO

import de.htwg.werwolf.model.commandComponent.GameMemento

trait IOInterface:
  def write(path: java.nio.file.Path, data: GameMemento): Unit
  def read(path: java.nio.file.Path): GameMemento
