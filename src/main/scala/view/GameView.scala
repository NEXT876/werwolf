// src/main/scala/view/GameView.scala
package de.htwg.werwolf.view

trait GameView:
  def getPlayerAmount(): Int
  def getPlayerNames(playerAmount: Int): Vector[String]
  def printPlayerRoles(playerRoles: Vector[AnyRef]): Unit
  def tiping(text: String, waitTime_ms: Int): Unit
  def showLogo(): Unit
  def clearScreen(): Unit
