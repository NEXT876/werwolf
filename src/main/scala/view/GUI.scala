package de.htwg.werwolf.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout._
import scalafx.scene.paint.Color._
import scalafx.scene.shape._
import scalafx.scene.text.Text
import scalafx.scene.effect.DropShadow
import scalafx.geometry.Insets
import scalafx.animation.RotateTransition
import scalafx.util.Duration

object GUI extends JFXApp3 {

  override def start(): Unit = {

    // Rotierbares Oval
    val disk = new Ellipse {
      centerX = 400
      centerY = 300
      radiusX = 350
      radiusY = 180
      stroke = Black
      fill = White
      strokeWidth = 2
    }

    val rotateAnimation = new RotateTransition {
      node = disk
      duration = Duration(5000)
      byAngle = 360
      cycleCount = RotateTransition.Indefinite
    }
    rotateAnimation.play()

    // Helper: Box mit Rahmen & Text
    def box(label: String, w: Double = 150, h: Double = 60) = new VBox {
      padding = Insets(5)
      spacing = 5
      prefWidth = w
      prefHeight = h
      style = "-fx-border-color: black; -fx-background-color: white;"
      children = Seq(new Text(label))
    }

    val root = new Pane {
      children = Seq(
        disk,

        // Top left
        new VBox {
          layoutX = 20
          layoutY = 20
          children = Seq(box("amount of members per faction", 200, 80))
        },

        // Top center
        new VBox {
          layoutX = 300
          layoutY = 20
          children = Seq(box("day / night cycle", 300, 60))
        },

        // Top right
        new VBox {
          layoutX = 750
          layoutY = 20
          children = Seq(box("special information", 200, 80))
        },

        // Left middle (player4)
        new VBox {
          layoutX = 20
          layoutY = 250
          children = Seq(box("player4"))
        },

        // Bottom left (chat)
        new VBox {
          layoutX = 20
          layoutY = 430
          children = Seq(box("chat | team chat", 220, 200))
        },

        // Bottom middle (player1)
        new VBox {
          layoutX = 380
          layoutY = 530
          children = Seq(box("player1"))
        },

        // Right middle (player2)
        new VBox {
          layoutX = 900
          layoutY = 250
          children = Seq(box("player2"))
        },

        // Bottom right (rollen info)
        new VBox {
          layoutX = 900
          layoutY = 430
          children = Seq(box("rollen info", 220, 200))
        },

        // Top middle below cycle (player3)
        new VBox {
          layoutX = 420
          layoutY = 120
          children = Seq(box("player3"))
        }
      )
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Werwolf GUI"
      scene = new Scene(root, 1150, 700)
    }
  }
}
