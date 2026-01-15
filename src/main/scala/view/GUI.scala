package de.htwg.werwolf.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape._
import scalafx.scene.text.{Text, TextAlignment}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.StringProperty
import scalafx.application.Platform
import scalafx.stage.Screen
import scalafx.Includes.*
import scalafx.util.Duration
import scalafx.animation.{FillTransition, Timeline, KeyFrame}

import scala.compiletime.uninitialized

import de.htwg.werwolf.model.GameEvent
import de.htwg.werwolf.controller.GameControllerInterface
import de.htwg.werwolf.util.Observer

/** GUI mit Vorabbefragung der Spielerzahl + Namen
  *
  * Flow: 1) Setup-View (Anzahl wählen) 2) Namenseingabe-Formular (genau N Felder) 3) Bei
  * Bestätigung -> Spiel wird gebaut (UI wechselt) und controller.addRoles(names) +
  * controller.runGame() werden aufgerufen
  *
  * Hinweise:
  *   - Diese GUI erwartet, dass ein GameControllerInterface via `init()(using controller)`
  *     injiziert wird
  *   - Die TUI bleibt unverändert, wird aber nicht automatisch gestartet — GUI steuert das Spiel
  */
object GUI extends JFXApp3 with Observer[GameEvent] {
  // Controller wird per init injiziert (wie in deiner Umgebung)
  var controller: GameControllerInterface = uninitialized

  private var mainRoot: BorderPane = _
  private var titleText: Text = _

  // Observable Strings für dynamische Anzeige
  private val rollenInfoText = StringProperty("Noch keine Rolleninformationen vorhanden")
  private val NightDayCycleText = StringProperty("Warte auf Spieler...")
  private val FactionAmountText = StringProperty("Werwölfe: 0\nDorfbewohner: 0")
  private val SpecialInformationText = StringProperty("")

  private lazy val factionArea = new TextArea {
    editable = false
    text <== FactionAmountText
    wrapText = true
    prefRowCount = 6
  }

  private lazy val specialInfo = new TextArea {
    editable = false
    text <== SpecialInformationText
    wrapText = true
    prefRowCount = 6
  }

  private lazy val rollenInfoField = new TextArea {
    editable = false
    text <== rollenInfoText
    wrapText = true
    prefRowCount = 8
  }

  def init()(using c: GameControllerInterface): Unit =
    controller = c
    controller.addObserver(this)

  override def update(event: GameEvent): Unit =
    event match
      case GameEvent.printGameState(players) => printPlayerRoles(players)
      case GameEvent.switchPhase(phase) => Platform.runLater { NightDayCycleText.value = phase }
      case _                            => ()

  // --- UI nodes that we will swap ---
  private var setupPane: VBox = _
  private var nameEntryPane: VBox = _

  override def start(): Unit =
    // Top menu / title (always visible)
    val menuBar = new MenuBar {
      menus = List(new Menu("Spiel") {
        items = List(
          new MenuItem("Spiel speichern") {
            onAction => controller.saveGameState()
          },
          new MenuItem("Wiederherstellen") {
            onAction => controller.undoFull()
          }
        )
      })
      styleClass += "top-menu"
    }

    titleText = new Text("Werwolf — Setup") { styleClass += "app-title" }
    val topBar = new HBox {
      padding = Insets(12)
      alignment = Pos.CenterLeft
      children = Seq(titleText)
      styleClass += "topbar"
    }

    // --- SETUP PANE: Anzahl auswählen ---
    val playerCountChoices = ObservableBuffer(2, 3, 4, 5, 6)
    val playerCountCombo = new ComboBox[Int](playerCountChoices) {
      value = 4 // default
      prefWidth = 120
    }

    val countNextBtn = new Button("Weiter") {
      styleClass ++= Seq("btn", "btn-primary")
      onAction = _ => {
        val count = Option(playerCountCombo.value.value).getOrElse(4)
        showNameEntry(count)
      }
    }

    setupPane = new VBox(12) {
      padding = Insets(20)
      alignment = Pos.Center
      children = Seq(
        new Label("Wähle die Anzahl der Spieler (2–6)"),
        playerCountCombo,
        countNextBtn
      )
      styleClass += "card"
    }

    // --- Placeholder center: wir zeigen zuerst setupPane ---
    val centerContainer = new StackPane {
      padding = Insets(12)
      children = Seq(setupPane)
    }

    // --- rightColumn (Info) -- initial schmal / informativ ---
    val dayNight = new Label {
      text <== NightDayCycleText
      styleClass += "big-phase"
    }

    val nextBtn = new Button("Nächste Runde") {
      styleClass ++= Seq("btn", "btn-accent")
      onAction = _ => () // Spielsteuerung später
    }

    val rightColumn = new VBox(12) {
      padding = Insets(12)
      prefWidth = 360 // initial, kann angepasst werden
      minWidth = 240
      children = Seq(
        new Label("Phase"),
        dayNight,
        new Label("Fraktionen"),
        factionArea,
        new Label("Spezial"),
        specialInfo,
        new Label("Rollen"),
        rollenInfoField,
        new HBox { spacing = 8; children = Seq(nextBtn) }
      )
      styleClass += "card"
    }

    // --- Root BorderPane (setup view shown in center) ---
    mainRoot = new BorderPane {
      top = new VBox(menuBar, topBar)
      center = centerContainer
      right = rightColumn
      padding = Insets(10)
      styleClass += "root-pane"
    }

    // --- Stage / Scene ---
    stage = new JFXApp3.PrimaryStage {
      title = "Werwolf — Setup"
      val bounds = Screen.primary.visualBounds
      width = bounds.width * 0.92
      height = bounds.height * 0.88
      scene = new Scene(mainRoot) {
        val cssUrl = getClass.getResource("/modern_style.css")
        if cssUrl != null then stylesheets.add(cssUrl.toExternalForm)
        fill = Color.web("#f4f7f9")
      }
    }

  /** Zeige das Formular zur Namenseingabe für `count` Spieler */
  private def showNameEntry(count: Int): Unit = {
    val nameFields = (0 until count).map { i =>
      new TextField {
        promptText = s"Spieler ${i + 1}"
        prefWidth = 280
      }
    }

    val startBtn = new Button("Spiel starten") {
      styleClass ++= Seq("btn", "btn-primary")
      onAction = _ => {
        // Validierung: keine leeren Namen (oder Standardnamen vergeben)
        val rawNames = nameFields.map(_.text.value.trim)
        val names = rawNames.zipWithIndex.map { case (n, i) =>
          if n.isEmpty then s"Spieler${i + 1}" else n
        }

        // we add roles (controller will assign roles based on names)
        controller.addRoles(names.toVector)
        // set displayed phase
        NightDayCycleText.value = "Tag/Nacht: Initialisiert"
        // Build game UI now that we have names
        buildGameUI(names)
        // Start or run the game loop in controller
        new Thread {
          override def run(): Unit = controller.runGame()
        }.start()
      }
    }

    val backBtn = new Button("Zurück") {
      onAction = _ =>
        // return to setup pane
        mainRoot.center = new StackPane { children = Seq(setupPane) }
    }

    nameEntryPane = new VBox(10) {
      padding = Insets(16)
      alignment = Pos.Center
      children =
        Seq(new Label(s"Gib die Namen der $count Spieler ein:")) ++ nameFields ++ Seq(new HBox(8) {
          children = Seq(startBtn, backBtn)
        })
      styleClass += "card"
    }

    // Swap center to nameEntryPane
    mainRoot.center = new StackPane { children = Seq(nameEntryPane) }
  }

  /** Baut das eigentliche Spiel-UI basierend auf den übergebenen Spielernamen. Wird erst
    * aufgerufen, wenn Namen vorliegen.
    */
  private def buildGameUI(namesSeq: Seq[String]): Unit = {
    // Update title
    titleText.text = "Werwolf — Spiel"

    // --- Left: Chat + Players ---
    val chatMessages = ObservableBuffer.empty[String]
    val chatListView = new ListView(chatMessages) {
      prefWidth = 320
      prefHeight = 380
      styleClass += "chat-list"
    }

    val chatInput = new TextField {
      promptText = "Nachricht schreiben... (Enter zum Senden)"
      prefWidth = 240
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
          chatInput.text = ""
    }

    val chatBox = new VBox(8) {
      padding = Insets(12)
      children =
        Seq(new Label("Chat"), chatListView, new HBox(8) { children = Seq(chatInput, sendBtn) })
      styleClass += "card"
    }

    // Spieler-Panel (Avatare)
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
        children = Seq(new StackPane { children = Seq(avatar) }, label)
      }
      new StackPane { children = Seq(v); padding = Insets(6); styleClass += "player-card" }
    }

    val playersFlow = new FlowPane {
      hgap = 8
      vgap = 8
      prefWrapLength = 320
      children = namesSeq.map(name => playerNode(name, true))
    }

    val playersPanel = new VBox(10) {
      padding = Insets(12)
      children = Seq(new Label("Spieler"), playersFlow)
      styleClass += "card"
    }

    val leftColumn = new VBox(12) {
      padding = Insets(10)
      prefWidth = 340
      children = Seq(chatBox, playersPanel)
    }

    // --- Center: Oval playfield with players positioned on ellipse ---
    val ovalRadiusX = 330
    val ovalRadiusY = 160

    val disk = new Ellipse {
      radiusX = ovalRadiusX
      radiusY = ovalRadiusY
      stroke = Color.web("#263238")
      strokeWidth = 2
      fill = Color.web("#ffffff00")
    }

    val centerPane = new AnchorPane {
      prefWidth = 760
      prefHeight = 520
      children = Seq(disk)
      styleClass += "playfield"
    }

    disk.layoutX = centerPane.prefWidth.value / 2
    disk.layoutY = centerPane.prefHeight.value / 2

    // build player nodes and position on oval
    var playerNodes: Seq[StackPane] = Seq()
    def createPlayersOnOval(names: Seq[String]): Unit = {
      centerPane.children --= playerNodes.map(_.delegate)
      playerNodes = names.map { n =>
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
      val n = Math.max(1, playerNodes.length)
      for ((p, i) <- playerNodes.zipWithIndex) {
        val angle = Math.toRadians(baseAngleDeg + i * (360.0 / n))
        val x = cx + ovalRadiusX * Math.cos(angle) - p.prefWidth() / 2
        val y = cy + ovalRadiusY * Math.sin(angle) - p.prefHeight() / 2
        p.layoutX = x
        p.layoutY = y
      }
    }

    createPlayersOnOval(namesSeq)

    // optional: rotate players (keeps table dynamic)
    var currentAngle = 0.0
    def rotatePlayers(steps: Int = 1): Unit = {
      val stepAngle = 360.0 / Math.max(1, playerNodes.length) * steps
      val animSteps = 8
      val stepDur = 30
      val kfSeq = (1 to animSteps).map { k =>
        KeyFrame(
          Duration(stepDur * k),
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

    // --- Right: reuse the existing but adjust if necessary ---
    val rightPanel = new VBox(12) {
      padding = Insets(12)
      prefWidth = 360
      minWidth = 240
      children = Seq(
        new Label("Phase"),
        new Label {
          text <== NightDayCycleText
          styleClass += "big-phase"
        },
        new Label("Fraktionen"),
        factionArea,
        new Label("Spezial"),
        specialInfo,
        new Label("Rollen"),
        rollenInfoField,
        new HBox {
          spacing = 8
          children = Seq(
            new Button("Nächste Runde") {
              onAction = _ => rotatePlayers(1)
            }
          )
        }
      )
      styleClass += "card"
    }

    // Update mainRoot for game
    mainRoot.left = leftColumn
    mainRoot.center = centerPane
    mainRoot.right = rightPanel

    // small pulsing animation for disk
    val colorAnimation = new FillTransition {
      shape = disk
      duration = Duration(4000)
      fromValue = Color.web("#fafafa")
      toValue = Color.web("#f1f8f7")
      cycleCount = FillTransition.Indefinite
      autoReverse = true
    }
    colorAnimation.play()
  }

  // Helper to display player roles string
  private def printPlayerRoles(playerRoles: String): Unit = {
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
}
