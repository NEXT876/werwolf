package de.htwg.werwolf.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape.*
import scalafx.scene.text.Text
import scalafx.scene.effect.DropShadow
import scalafx.geometry.Insets
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.*
import scalafx.scene.Group
import scalafx.Includes.*
import scalafx.animation.{RotateTransition, FillTransition, Timeline, KeyFrame}
import scalafx.util.Duration

import scala.compiletime.uninitialized

import de.htwg.werwolf.util.*
import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.controller.*
import scalafx.beans.property.StringProperty
import scalafx.application.Platform

object GUI extends JFXApp3 with Observer[GameEvent] {
  var controller: GameController = uninitialized // Controller injizieren

  private val rollenInfoText = StringProperty("Willkommen zu Werwolf!")
  private val NightDayCycleText = StringProperty("Nacht")


  def init(c: GameController): Unit = {
    controller = c
    controller.addObserver(this) // GUI als Observer registrieren
  }

  override def start(): Unit = {

    println("[GUI] JavaFX Thread gestartet – Fenster wird jetzt gebaut...")
    // --- Helper: Box mit Rahmen, Schatten & Hover-Effekt ---
    def box(label: String, w: Double = 150, h: Double = 60): VBox = {
      val b = new VBox {
        padding = Insets(10)
        spacing = 8
        prefWidth = w
        prefHeight = h
        style =
          "-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 10; -fx-background-radius: 10;"
        effect = new DropShadow {
          offsetX = 3
          offsetY = 3
          color = Color.LightGray
          radius = 5
        }

        children = Seq(new Text(label) {
          style = "-fx-font-weight: bold; -fx-font-size: 14px;"
        })
      }
      b.onMouseEntered = (_: MouseEvent) =>
        b.style =
          "-fx-background-color: #e0f7fa; -fx-border-color: #00acc1; -fx-border-radius: 10; -fx-background-radius: 10;"
      b.onMouseExited = (_: MouseEvent) =>
        b.style =
          "-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 10; -fx-background-radius: 10;"
      // Box rückgabe
      b
    }

    val rollenInfoField = new TextArea {
      text <== rollenInfoText // Automatisches Binding
      editable = false
      wrapText = true
      prefRowCount = 8
    }

    def createRollenInfoBox(): VBox = new VBox {
      padding = Insets(10)
      children = Seq(
        new Text("rollen info"),
        rollenInfoField
      )
    }

    // --- Chatbox zuerst definieren ---
    val chatArea = new TextArea {
      prefWidth = 200
      prefHeight = 150
      editable = false
      styleClass.add("chat-textarea")
    }
    val chatInput = new TextField {
      prefWidth = 200
      styleClass.add("chat-input")
    }
    val sendButton = new Button("Send")
    sendButton.onAction = _ => {
      val msg = chatInput.text.value
      if msg.nonEmpty then
        chatArea.appendText(s"You: $msg\n")
        chatInput.clear()
    }
    val chatBox = new VBox(5) {
      children = Seq(chatArea, chatInput, sendButton)
    }

    // --- Drehendes Oval ---
    val ovalRadiusX = 350
    val ovalRadiusY = 180

    val disk = new Ellipse {
      centerX = 0
      centerY = 0
      radiusX = ovalRadiusX
      radiusY = ovalRadiusY
      stroke = Color.DarkGray
      fill = Color.LightGray
      strokeWidth = 3
      effect = new DropShadow {
        offsetX = 5
        offsetY = 5
        color = Color.Gray
        radius = 10
      }
    }

    val ovalGroup = new Group(disk)

    // --- Spieler-Boxen auf Oval ---
    val players = Seq(box("player1"), box("player2"), box("player3"), box("player4"))
    ovalGroup.children.addAll(players.map(_.delegate): _*)

    // Spielerpositionierung
    var currentAngle = 0.0
    def positionPlayers(angleDeg: Double): Unit = {
      for ((player, idx) <- players.zipWithIndex) {
        val angle = Math.toRadians(angleDeg + idx * 90)
        player.layoutX = ovalRadiusX * Math.cos(angle) - player.prefWidth.value / 2
        player.layoutY = ovalRadiusY * Math.sin(angle) - player.prefHeight.value / 2
      }
    }
    positionPlayers(currentAngle)

    // --- Steuer-Buttons unten Mitte ---
    val nextButton = new Button("nächster")
    val skipButton = new Button("skip")

    nextButton.onAction = _ => {
      val steps = 5
      val stepAngle = 90.0 / steps
      val stepDuration = 50 // ms pro Schritt

      var stepCount = 0

      // KeyFrames in einer lokalen Variablen anders nennen
      val kfSeq = (1 to steps).map { _ =>
        KeyFrame(
          Duration(stepDuration),
          onFinished = _ => {
            currentAngle += stepAngle
            if (currentAngle >= 360) currentAngle -= 360
            positionPlayers(currentAngle)
          }
        )
      }

      val animTimeline = new Timeline {
        keyFrames = kfSeq
      }

      animTimeline.onFinished = _ => {
        currentAngle = Math.round(currentAngle / 90) * 90
        positionPlayers(currentAngle)
      }

      animTimeline.play()
    }

    skipButton.onAction = _ => {
      // keine Aktion
    }

    val bottomCenterButtons = new HBox(10) {
      children = Seq(nextButton, skipButton)
      alignment = scalafx.geometry.Pos.Center
    }

    // --- Top Bar ---
    val topLeft = box("amount of members per faction", 200, 80)
    val topCenter = box("day / night cycle", 300, 60)
    val topRight = box("special information", 200, 80)

    val topPane = new BorderPane {
      left = topLeft
      center = topCenter
      right = topRight
      padding = Insets(10)
    }

    // --- Bottom Pane ---
    val bottomPane = new BorderPane {
      left = chatBox
      center = bottomCenterButtons
      right = createRollenInfoBox()
      padding = Insets(10)
    }

    // --- Root Layout ---
    val root = new BorderPane {
      top = topPane
      bottom = bottomPane
      center = new StackPane {
        children = Seq(ovalGroup)
      }
      padding = Insets(10)
    }

    // --- Oval-Farbwechsel ---
    val colorAnimation = new FillTransition {
      shape = disk
      duration = Duration(4000)
      fromValue = Color.LightGray
      toValue = Color.LightBlue
      cycleCount = FillTransition.Indefinite
      autoReverse = true
    }
    colorAnimation.play()

    // --- Scene & CSS ---
    stage = new JFXApp3.PrimaryStage {
      title = "Werwolf GUI"
      scene = new Scene(root, 1200, 800) {
        fill = Color.rgb(153, 146, 146, 1)
        stylesheets.add("style.css")
      }
    }
  }

  override def update(event: GameEvent): Unit =
    event match
      case GameEvent.printGameState(players) =>
        printPlayerRoles(players.map { case (name, player) =>
          (name, player.role.toString(), player.isAlive)
        }.toVector)

      case _ =>

  def printPlayerRoles(playerRoles: Vector[AnyRef]): Unit = {
    val header = "\n================ Spieler & Rollen ================\n"

    val body = playerRoles
      .map {
        case (name: String, role: String, isAlive: Boolean) =>
          val state = if isAlive then "lebt" else "tot"
          f"• ${name}%-15s | Rolle: ${role}%-10s | Status: ${state}%-7s"

        case other =>
          s"• Unbekanntes Objekt: ${other.getClass.getSimpleName}"
      }
      .mkString("\n")

    val footer = "\n==================================================\n"

     // WICHTIG: Platform.runLater für UI-Thread
    Platform.runLater {
      rollenInfoText.value = header + body + footer
    }
  }
}
