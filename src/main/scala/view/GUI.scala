package de.htwg.werwolf.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape._
import scalafx.scene.text.{Font, Text, TextAlignment}
import scalafx.scene.effect.DropShadow
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.input.MouseEvent
import scalafx.scene.control._
import scalafx.scene.Group
import scalafx.Includes.*
import scalafx.animation.{RotateTransition, FillTransition, Timeline, KeyFrame}
import scalafx.util.Duration
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.StringProperty
import scalafx.application.Platform
import scalafx.stage.Screen

import scala.compiletime.uninitialized

import de.htwg.werwolf.util.*
import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.controller.*

/** Modernisierte GUI
  *   - Externe CSS: modern_style.css (in resources)
  *   - Bessere Struktur: TopBar, Left=Chat/Players, Center=Table (oval), Right=Info/Controls
  *   - Nutzt ListView für Chat, Avatar-Circles für Spieler
  */
object GUI extends JFXApp3 with Observer[GameEvent] {
  var controller: GameControllerInterface = uninitialized // Controller injizieren

  private val rollenInfoText = StringProperty("Noch keine Rolleninformationen vorhanden")
  private val NightDayCycleText = StringProperty("Nacht")
  private val FactionAmountText = StringProperty("Werwölfe: 0\nDorfbewohner: 0")
  private val SpecialInformationText = StringProperty("Keine Spezialinformationen")

  def init()(using c: GameControllerInterface): Unit =
    controller = c
    controller.addObserver(this)

  override def update(event: GameEvent): Unit =
    event match
      case GameEvent.printGameState(players) =>
        printPlayerRoles(players)
      case GameEvent.switchPhase(phase) =>
        Platform.runLater { NightDayCycleText.value = phase }
      case _ =>

  override def start(): Unit =

    // --- Menu Bar (Save / Restore) ---
    val saveItem = new MenuItem("Save Game")
    saveItem.onAction = _ => {
      controller.saveIntoFile("test_1")
    }

    val restoreItem = new MenuItem("Spiel wiederherstellen")
    restoreItem.onAction = _ => controller.undoFull()

    val menu = new Menu("Spiel") { items = List(saveItem, restoreItem) }
    val menuBar = new MenuBar { menus = List(menu) }
    menuBar.getStyleClass.add("top-menu")

    // --- Top Bar / Titel ---
    val titleText = new Text("Werwolf — Modern GUI") {
      styleClass += "app-title"
      wrappingWidth = 600
    }
    val topBar = new HBox {
      padding = Insets(12)
      alignment = Pos.CenterLeft
      children = Seq(titleText)
      styleClass += "topbar"
    }

    // --- Chat (Left) ---
    val chatMessages = ObservableBuffer.empty[String]
    val chatListView = new ListView(chatMessages) {
      prefWidth = 300
      prefHeight = 380
      styleClass += "chat-list"
    }

    val chatInput = new TextField {
      promptText = "Nachricht schreiben... (Enter zum Senden)"
      prefWidth = 220
      styleClass += "chat-input"
      onAction = _ => {
        val msg = text.value
        if msg != null && msg.trim.nonEmpty then
          chatMessages += s"Du: ${msg.trim}"
          text.value = ""
      }

    }

    val sendBtn = new Button("Senden") {
      styleClass ++= Seq("btn", "btn-primary")
      onAction = _ =>
        val txt = chatInput.text.value
        if txt != null && txt.trim.nonEmpty then
          chatMessages += s"Du: ${txt.trim}"
          chatInput.clear()
    }

    val chatBox = new VBox(8) {
      padding = Insets(12)
      children =
        Seq(new Label("Chat"), chatListView, new HBox(8) { children = Seq(chatInput, sendBtn) })
      styleClass += "card"
    }

    // --- Spieler-Panel (Left unter Chat) ---
    // Hilfsfunktion für Player-Node (Avatar + Name)
    def playerNode(name: String, alive: Boolean = true): StackPane = {
      val avatar = new Circle {
        radius = 28
        stroke = Color.web("#0e7a7a")
        strokeWidth = 2
        fill = if alive then Color.web("#e0f7f5") else Color.web("#dddddd")
      }
      val label = new Label(name) {
        wrapText = true
        maxWidth = 90
        textAlignment = TextAlignment.Center
        styleClass += "player-label"
      }
      val v = new VBox(6) {
        alignment = Pos.Center
        children = Seq(new StackPane { children = avatar :: Nil }, label)
      }
      val root = new StackPane { children = v; padding = Insets(6) }
      root.getStyleClass.add("player-card")
      root
    }

    val playersList = new FlowPane {
      hgap = 8
      vgap = 8
      prefWrapLength = 300
      children = Seq(
        playerNode("Spieler1"),
        playerNode("Spieler2"),
        playerNode("Spieler3"),
        playerNode("Spieler4")
      )
    }

    val playersPanel = new VBox(10) {
      padding = Insets(12)
      children = Seq(new Label("Spieler"), playersList)
      styleClass += "card"
    }

    val leftColumn = new VBox(12) {
      padding = Insets(10)
      prefWidth = 320
      children = Seq(chatBox, playersPanel)
    }

    // --- Center: Oval Playfield mit Player Nodes ---
    val ovalRadiusX = 330
    val ovalRadiusY = 160

    val disk = new Ellipse {
      radiusX = ovalRadiusX
      radiusY = ovalRadiusY
      stroke = Color.web("#263238")
      strokeWidth = 2
      fill = Color.web("#ffffff00") // transparent center
    }

    val centerPane = new AnchorPane {
      prefWidth = 760
      prefHeight = 520
      children += disk
      styleClass += "playfield"
    }

    // Positionieren: disk zentriert
    disk.layoutX = centerPane.prefWidth.value / 2
    disk.layoutY = centerPane.prefHeight.value / 2

    // Spieler als Nodes auf Ellipse
    var playerNodes: Seq[StackPane] = Seq()
    def createPlayersOnOval(names: Seq[String]): Unit = {
      // Entferne alte
      centerPane.children --= playerNodes.map(_.delegate)
      playerNodes = names.zipWithIndex.map { case (n, idx) =>
        val p = playerNode(n)
        p.layoutX = 0
        p.layoutY = 0
        p
      }
      centerPane.children.addAll(playerNodes.map(_.delegate): _*)
      positionPlayersOnOval(0)
    }

    def positionPlayersOnOval(baseAngleDeg: Double): Unit = {
      val cx = disk.layoutX.value
      val cy = disk.layoutY.value
      val n = playerNodes.length
      for ((p, i) <- playerNodes.zipWithIndex) {
        val angle = Math.toRadians(baseAngleDeg + i * (360.0 / Math.max(1, n)))
        val x = cx + ovalRadiusX * Math.cos(angle) - p.width.value / 2
        val y = cy + ovalRadiusY * Math.sin(angle) - p.height.value / 2
        p.layoutX = x
        p.layoutY = y
      }
    }

    createPlayersOnOval(Seq("Anna", "Ben", "Carla", "David"))

    // Rotate animation for center (optional)
    var currentAngle = 0.0
    def rotatePlayers(steps: Int = 1): Unit = {
      val stepAngle = 360.0 / Math.max(1, playerNodes.length) * steps
      val animSteps = 8
      val stepDur = 30
      val kfSeq = (1 to animSteps).map { _ =>
        KeyFrame(
          Duration(stepDur),
          onFinished = _ => {
            currentAngle += stepAngle / animSteps
            positionPlayersOnOval(currentAngle)
          }
        )
      }
      val tl = new Timeline { keyFrames = kfSeq }
      tl.onFinished = _ =>
        currentAngle = Math.round(
          currentAngle / (360.0 / Math.max(1, playerNodes.length))
        ) * (360.0 / Math.max(1, playerNodes.length))
      tl.play()
    }

    // --- Right: Info & Controls ---
    val factionArea = new TextArea {
      editable = false
      text <== FactionAmountText
      wrapText = true
      prefRowCount = 6
    }

    val dayNight = new Label {
      text <== NightDayCycleText
      styleClass += "big-phase"
    }

    val specialInfo = new TextArea {
      editable = false
      text <== SpecialInformationText
      wrapText = true
      prefRowCount = 6
    }

    val rollenInfoField = new TextArea {
      editable = false
      text <== rollenInfoText
      wrapText = true
      prefRowCount = 8
    }

    val nextBtn = new Button("Nächste Runde") {
      styleClass ++= Seq("btn", "btn-accent")
      onAction = _ => rotatePlayers(1)
    }
    val saveBtn = new Button("Speichern") {
      styleClass ++= Seq("btn", "btn-outline"); onAction = _ => controller.saveGameState()
    }
    val restoreBtn = new Button("Wiederherstellen") {
      styleClass ++= Seq("btn", "btn-outline"); onAction = _ => controller.undoFull()
    }

    val controls = new VBox(10) {
      children = Seq(nextBtn, new HBox(8) { children = Seq(saveBtn, restoreBtn) })
      alignment = Pos.Center
      padding = Insets(8)
    }

    val rightColumn = new VBox(12) {
      padding = Insets(12)
      prefWidth = 500
      children = Seq(
        new Label("Phase"),
        dayNight,
        new Label("Fraktionen"),
        factionArea,
        new Label("Spezial"),
        specialInfo,
        new Label("Rollen"),
        rollenInfoField,
        controls
      )
      styleClass += "card"
    }

    // --- Root Layout ---
    val root = new BorderPane {
      top = new VBox(menuBar, topBar)
      left = leftColumn
      center = new StackPane {
        padding = Insets(12)
        children = Seq(centerPane)
      }

      right = rightColumn
      padding = Insets(10)
      styleClass += "root-pane"
    }

    // --- Disk Color Animation (subtiler Puls) ---
    val colorAnimation = new FillTransition {
      shape = disk
      duration = Duration(5000)
      fromValue = Color.web("#fafafa")
      toValue = Color.web("#f1f8f7")
      cycleCount = FillTransition.Indefinite
      autoReverse = true
    }
    colorAnimation.play()

    // --- Stage / Scene ---
    stage = new JFXApp3.PrimaryStage {
      title = "Werwolf — GUI (modern)"
      val bounds = Screen.primary.visualBounds
      width = bounds.width * 0.92
      height = bounds.height * 0.88

      scene = new Scene(root) {
        // stylesheets: lade die CSS aus resources — lege modern_style.css in resources
        val cssUrl = getClass.getResource("/modern_style.css")
        if cssUrl != null then stylesheets.add(cssUrl.toExternalForm)
        else stylesheets.add("modern_style.css") // fallback

        fill = Color.web("#f4f7f9")
      }
    }

  def printPlayerRoles(playerRoles: String): Unit =
    val header = "\n================ Spieler & Rollen ================\n"
    val footer = "\n==================================================\n"
    val (aliveWerwolves, aliveVillagers) =
      try controller.countAlivePlayer()
      catch { case _: Throwable => (0, 0) }

    Platform.runLater {
      rollenInfoText.value = header + playerRoles + footer
      FactionAmountText.value = s"Werwölfe : $aliveWerwolves\nDorfbewohner : $aliveVillagers"
    }
}
