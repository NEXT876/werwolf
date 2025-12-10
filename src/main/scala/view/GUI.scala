package de.htwg.werwolf.view

// src/main/scala/HelloScalaFX.scala
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.paint.Color.*
import scalafx.scene.text.Text

object ScalaFXHelloWorld extends JFXApp3:
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage:
      title = "ScalaFX Hello World"
      scene = new Scene:
        fill = Black
        content = new HBox:
          padding = Insets(20)
          children = Seq(
            new Text:
              text = "Hello "
              style = "-fx-font-size: 48pt"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(PaleGreen, SeaGreen)
              ),
            new Text:
              text = "World!!!"
              style = "-fx-font-size: 48pt"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(Cyan, DodgerBlue)
              )
              effect = new DropShadow:
                color = DodgerBlue
                radius = 25
                spread = 0.25
          )