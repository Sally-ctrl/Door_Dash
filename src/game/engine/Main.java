package game.engine;
import game.engine.cards.Card;
import game.engine.cards.ConfusionCard;
import game.engine.cards.EnergyStealCard;
import game.engine.cards.ShieldCard;
import game.engine.cards.StartOverCard;
import game.engine.cards.SwapperCard;
import game.engine.monsters.*;
import javafx.scene.image.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.*;
import javafx.util.Duration;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.beans.binding.*;




public class Main extends Application {
    private GameController controller;
    private Game game;
    private MediaPlayer currentMusic;
    private NumberBinding cellSize;
    private ImageView playerToken;
    private ImageView opponentToken;
    private Pane overlay;        
    private GridPane board;  
    private VBox playerTeamBox;
    private VBox opponentTeamBox;
   private Label cardsRemainingLabel;
    private Label reshuffledLabel;
    private int playerTokenIndex = 0;
    private int opponentTokenIndex = 0;
    private boolean isMuted = false;
    

    public void start(Stage stage){
        System.err.println("Running from: " + new java.io.File("").getAbsolutePath());
        System.err.println("Cards exists: " + new java.io.File("cards.csv").exists());
        System.err.println("Monsters exists: " + new java.io.File("monsters.csv").exists());
        this.controller = new GameController(this);
        stage.setMinHeight(700);
        stage.setMinWidth(1000);
        stage.setMaximized(true);
        WelcomeStage(stage);
        stage.show();
    }


   public void WelcomeStage(Stage stage) {
    playMusic("/game/audio/welcome.mp3");
    stage.setTitle("Door Dash: Scare vs Laugh Touchdown");

    // ── floating door particles canvas ──────────────────────
    javafx.scene.canvas.Canvas particles = new javafx.scene.canvas.Canvas(1600, 900);
    javafx.scene.canvas.GraphicsContext gc = particles.getGraphicsContext2D();

    java.util.List<double[]> doors = new java.util.ArrayList<>();
    Random rng = new Random();
    for (int i = 0; i < 18; i++) {
        doors.add(new double[]{
            rng.nextDouble() * 1600,
            rng.nextDouble() * 900,
            18 + rng.nextDouble() * 28,
            0.3 + rng.nextDouble() * 0.7,
            rng.nextDouble(),
            rng.nextBoolean() ? 1 : -1
        });
    }

    Timeline particleLoop = new Timeline(new KeyFrame(Duration.millis(30), e -> {
        gc.clearRect(0, 0, particles.getWidth(), particles.getHeight());
        for (double[] d : doors) {
            d[1] -= d[3];
            d[4] += d[5] * 0.012;
            if (d[4] > 0.55) d[5] = -1;
            if (d[4] < 0.05) { d[5] = 1; d[1] = 920; d[0] = rng.nextDouble() * 1600; }
            if (d[1] < -50) { d[1] = 920; d[0] = rng.nextDouble() * 1600; }

            double s = d[2], x = d[0], y = d[1];
            double op = Math.max(0, Math.min(1, d[4]));
            gc.setGlobalAlpha(op * 0.35);
            gc.setFill(javafx.scene.paint.Color.web("#4fc3f7"));
            gc.fillRoundRect(x, y, s * 0.6, s, 3, 3);
            gc.setFill(javafx.scene.paint.Color.web("#ff6600"));
            gc.fillOval(x + s * 0.42, y + s * 0.48, s * 0.1, s * 0.1);
            gc.setGlobalAlpha(1.0);
        }
    }));
    particleLoop.setCycleCount(Timeline.INDEFINITE);
    particleLoop.play();

    // ── DOOR DASH title ─────────────────────────────────────
    Label title = new Label("DOOR DASH");
    title.setFont(Font.font("Impact", FontWeight.BOLD, 92));
    title.setStyle("-fx-text-fill: white;");

    javafx.scene.effect.DropShadow titleGlow = new javafx.scene.effect.DropShadow();
    titleGlow.setColor(javafx.scene.paint.Color.web("#ff6600"));
    titleGlow.setRadius(30);
    title.setEffect(titleGlow);
    Timeline titlePulse = new Timeline(
        new KeyFrame(Duration.ZERO,         new KeyValue(titleGlow.radiusProperty(), 18)),
        new KeyFrame(Duration.seconds(1.2), new KeyValue(titleGlow.radiusProperty(), 50))
    );
    titlePulse.setAutoReverse(true);
    titlePulse.setCycleCount(Timeline.INDEFINITE);
    titlePulse.play();

    // ── subtitle ────────────────────────────────────────────
    Label subtitle = new Label("SCARE  VS  LAUGH  TOUCHDOWN");
    subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    subtitle.setStyle("-fx-text-fill: #ffcc00;");

    // ── monsters group photo ─────────────────────────────────
    ImageView monstersPhoto = new ImageView(
        new Image(getClass().getResourceAsStream("/game/images/monsters.jpg"))
    );
    monstersPhoto.setFitWidth(620);
    monstersPhoto.setFitHeight(280);
    monstersPhoto.setPreserveRatio(true);
    monstersPhoto.setStyle(
        "-fx-effect: dropshadow(gaussian, #4fc3f7, 20, 0.4, 0, 0);"
    );

    // subtle float animation on the photo
    TranslateTransition photoFloat = new TranslateTransition(Duration.millis(2200), monstersPhoto);
    photoFloat.setByY(-10);
    photoFloat.setAutoReverse(true);
    photoFloat.setCycleCount(Timeline.INDEFINITE);
    photoFloat.play();

    // ── PLAY button with PULSATING scale ────────────────────
    Button playButton = new Button("▶   ENTER THE FLOOR");
    playButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
    playButton.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 16 52 16 52;" +
        "-fx-background-radius: 40;" +
        "-fx-cursor: hand;"
    );

    javafx.scene.effect.DropShadow btnGlow = new javafx.scene.effect.DropShadow();
    btnGlow.setColor(javafx.scene.paint.Color.web("#ff6600"));
    btnGlow.setRadius(20);
    playButton.setEffect(btnGlow);

    // scale pulse — the PRESS ME energy
    ScaleTransition btnPulse = new ScaleTransition(Duration.millis(700), playButton);
    btnPulse.setFromX(1.0);
    btnPulse.setFromY(1.0);
    btnPulse.setToX(1.08);
    btnPulse.setToY(1.08);
    btnPulse.setAutoReverse(true);
    btnPulse.setCycleCount(Timeline.INDEFINITE);
    btnPulse.play();

    // glow pulse synced with scale
    Timeline glowPulse = new Timeline(
        new KeyFrame(Duration.ZERO,          new KeyValue(btnGlow.radiusProperty(), 15)),
        new KeyFrame(Duration.millis(700),   new KeyValue(btnGlow.radiusProperty(), 45))
    );
    glowPulse.setAutoReverse(true);
    glowPulse.setCycleCount(Timeline.INDEFINITE);
    glowPulse.play();

    playButton.setOnMouseEntered(e -> {
        btnPulse.stop();
        playButton.setScaleX(1.1);
        playButton.setScaleY(1.1);
        btnGlow.setRadius(55);
    });
    playButton.setOnMouseExited(e -> {
        playButton.setScaleX(1.0);
        playButton.setScaleY(1.0);
        btnGlow.setRadius(20);
        btnPulse.play();
    });
    playButton.setOnAction(e -> {
        particleLoop.stop();
        btnPulse.stop();
        glowPulse.stop();
        try {
            showTeamSelectScreen(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorAlert("Error: " + ex.getMessage());
        }
    });

    // ── HOW TO PLAY button ──────────────────────────────────
    Button howToButton = new Button("❓  HOW TO PLAY");
    howToButton.setFont(Font.font("Arial", 14));
    howToButton.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-text-fill: #aaaaaa;" +
        "-fx-border-color: #555555;" +
        "-fx-border-radius: 20;" +
        "-fx-background-radius: 20;" +
        "-fx-padding: 8 24 8 24;" +
        "-fx-cursor: hand;"
    );
    howToButton.setOnMouseEntered(e -> howToButton.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-text-fill: white;" +
        "-fx-border-color: #aaaaaa;" +
        "-fx-border-radius: 20;" +
        "-fx-background-radius: 20;" +
        "-fx-padding: 8 24 8 24;" +
        "-fx-cursor: hand;"
    ));
    howToButton.setOnMouseExited(e -> howToButton.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-text-fill: #aaaaaa;" +
        "-fx-border-color: #555555;" +
        "-fx-border-radius: 20;" +
        "-fx-background-radius: 20;" +
        "-fx-padding: 8 24 8 24;" +
        "-fx-cursor: hand;"
    ));
    howToButton.setOnAction(e -> showHowToPlay(stage));

    // ── tagline ─────────────────────────────────────────────
    Label tagline = new Label("\"We scare because we care.\"  ·  \"We laugh, that's our path.\"");
    tagline.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
    tagline.setStyle("-fx-text-fill: #555577; -fx-font-style: italic;");

    // ── assemble center ──────────────────────────────────────
    HBox bottomBtns = new HBox(12, howToButton, buildMuteButton());
    bottomBtns.setAlignment(Pos.CENTER);
    VBox center = new VBox(14, title, subtitle, monstersPhoto, playButton, bottomBtns, tagline);
    center.setAlignment(Pos.CENTER);
    center.setPadding(new Insets(40));

    // slide-up entrance
    center.setTranslateY(60);
    center.setOpacity(0);
    Timeline entrance = new Timeline(
        new KeyFrame(Duration.millis(700),
            new KeyValue(center.translateYProperty(), 0, javafx.animation.Interpolator.EASE_OUT),
            new KeyValue(center.opacityProperty(), 1, javafx.animation.Interpolator.EASE_OUT)
        )
    );
    entrance.play();

    // ── root ─────────────────────────────────────────────────
    StackPane root = new StackPane(particles, center);
    root.setStyle("-fx-background-color: #1a1a2e;");
    particles.widthProperty().bind(stage.widthProperty());
    particles.heightProperty().bind(stage.heightProperty());

    stage.setScene(new Scene(root));
    stage.setMaximized(true);
    stage.centerOnScreen();
}


    public void showTeamSelectScreen(Stage stage) {
        stage.setTitle("Door Dash: Scare vs Laugh Touchdown");
 
        Label title = new Label("CHOOSE YOUR SIDE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setStyle("-fx-text-fill: white;");
     
        Label subtitle = new Label("Who will prove their worth on the Floor?");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setStyle("-fx-text-fill: #cccccc;");


        VBox scarerCard = buildRoleCard(
            "SCARER",
            "/game/images/blue_guy.jpeg",
            "Harness the power of screams!\nTerrorize children for energy.\nBring fear to the Floor.",
            "#ff4444"
        );
     
        VBox laugherCard = buildRoleCard(
            "LAUGHER",
            "/game/images/green_guy.jpeg",
            "Revolutionize with laughter!\nLaughter produces 10x more energy.\nBring joy to the Floor.",
            "#44ff44"
        );
     
        scarerCard.setOnMouseClicked(e -> {
            try {
                controller.selectRole(Role.SCARER, stage);
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to load game data: " + ex.getMessage());
            }
        });
     
        laugherCard.setOnMouseClicked(e -> {
            try {
                controller.selectRole(Role.LAUGHER, stage);
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to load game data: " + ex.getMessage());
            }
        });
     
        HBox cardsRow = new HBox(60, scarerCard, laugherCard);
        cardsRow.setAlignment(Pos.CENTER);
     
        Button backButton = new Button("← Back");
        backButton.setFont(Font.font("Arial", 14));
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-border-color: #aaaaaa;" +
            "-fx-border-radius: 20;" +
            "-fx-padding: 8 24 8 24;"
        );
        backButton.setOnAction(e -> WelcomeStage(stage));
     
        HBox bottomRow = new HBox(12, backButton, buildMuteButton());
        bottomRow.setAlignment(Pos.CENTER);
        VBox root = new VBox(30, title, subtitle, cardsRow, bottomRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #1a1a2e;");
     
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.centerOnScreen();
    }


    private VBox buildRoleCard(String roleName, String imagePath,
            String description, String accentColor) {


        ImageView idCard = new ImageView(
            new Image(getClass().getResourceAsStream(imagePath))
        );
        idCard.setFitWidth(220);
        idCard.setFitHeight(160);
        idCard.setPreserveRatio(true);
       
        Label roleLabel = new Label(roleName);
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        roleLabel.setStyle("-fx-text-fill: " + accentColor + ";");
       
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 13));
        descLabel.setStyle("-fx-text-fill: #dddddd;");
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        descLabel.setMaxWidth(220);
       
        Label clickHint = new Label("Click to choose");
        clickHint.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        clickHint.setStyle("-fx-text-fill: " + accentColor + ";");
       
        VBox card = new VBox(14, idCard, roleLabel, descLabel, clickHint);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(280);
        card.setStyle(
            "-fx-background-color: #2a2a4e;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-cursor: hand;"
        );
       
        card.setOnMouseEntered(e ->
            card.setStyle(
                "-fx-background-color: #3a3a6e;" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + accentColor + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 20;" +
                "-fx-cursor: hand;"
            )
        );
        card.setOnMouseExited(e ->
            card.setStyle(
                "-fx-background-color: #2a2a4e;" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + accentColor + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;" +
                "-fx-cursor: hand;"
            )
        );


        return card;
    }


    public void Game(Stage stage) {
    playMusic("/game/audio/game.mp3");


    playerTokenIndex = 0;
    opponentTokenIndex = 0;

    Label[] playerPanelRefs = new Label[6];
    ProgressBar playerEnergyBar = new ProgressBar(0);
   
    Label[] opponentPanelRefs = new Label[6];
    ProgressBar opponentEnergyBar = new ProgressBar(0);


    board = new GridPane();
    board.setAlignment(Pos.CENTER);
    board.setGridLinesVisible(false);


    ImageView diceImageView = new ImageView();
    diceImageView.setFitWidth(64);
    diceImageView.setFitHeight(64);
    diceImageView.setPreserveRatio(true);
    controller.setDiceImageView(diceImageView);


    Label turnLabel = new Label("Current Turn:");
    turnLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
    turnLabel.setStyle("-fx-text-fill: #aaaaaa;");


    Label currentMonsterLabel = new Label(controller.getCurrentMonster().getName());
    currentMonsterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    currentMonsterLabel.setStyle("-fx-text-fill: white;");
    controller.setCurrentMonsterLabel(currentMonsterLabel);


    VBox turnInfo = new VBox(2, turnLabel, currentMonsterLabel);
    turnInfo.setAlignment(Pos.CENTER_LEFT);


    Button rollButton = new Button("🎲  ROLL DICE");
    rollButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    rollButton.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 12 28 12 28;" +
        "-fx-background-radius: 24;" +
        "-fx-cursor: hand;"
    );
    rollButton.setOnMouseEntered(e -> rollButton.setStyle(
        "-fx-background-color: #ff8833;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 12 28 12 28;" +
        "-fx-background-radius: 24;" +
        "-fx-cursor: hand;"
    ));
    rollButton.setOnMouseExited(e -> rollButton.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 12 28 12 28;" +
        "-fx-background-radius: 24;" +
        "-fx-cursor: hand;"
    ));


    Label powerupTitle = new Label("POWER UP");
    powerupTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
    powerupTitle.setStyle("-fx-text-fill: #1a1a2e;");


    Button powerupButton = new Button("-500 ⚡");
    powerupButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    powerupButton.setStyle(
        "-fx-background-color: #1a1a2e;" +
        "-fx-text-fill: #ffcc00;" +
        "-fx-padding: 8 20 8 20;" +
        "-fx-background-radius: 6;" +
        "-fx-cursor: hand;"
    );
    powerupButton.setOnMouseEntered(e -> powerupButton.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-text-fill: #ffcc00;" +
        "-fx-padding: 8 20 8 20;" +
        "-fx-background-radius: 6;" +
        "-fx-cursor: hand;"
    ));
    powerupButton.setOnMouseExited(e -> powerupButton.setStyle(
        "-fx-background-color: #1a1a2e;" +
        "-fx-text-fill: #ffcc00;" +
        "-fx-padding: 8 20 8 20;" +
        "-fx-background-radius: 6;" +
        "-fx-cursor: hand;"
    ));
    powerupButton.setOnAction(e -> {
        try {
            controller.usePowerup();
            showInfoAlert("Powerup activated for " + controller.getCurrentMonster().getName() + "!");
        } catch (Exception ex) {
            showErrorAlert(ex.getMessage());
        }
    });


    VBox powerupBox = new VBox(6, powerupTitle, powerupButton);
    powerupBox.setAlignment(Pos.CENTER);
    powerupBox.setPadding(new Insets(10, 16, 10, 16));
    powerupBox.setStyle(
        "-fx-background-color: #ffcc00;" +
        "-fx-border-color: #ffd700;" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 10;" +
        "-fx-background-radius: 10;"
    );


    HBox top = new HBox(30);
    top.setAlignment(Pos.CENTER_LEFT);
    top.setPadding(new Insets(0, 30, 0, 30));
    top.setPrefHeight(100);
    top.setStyle("-fx-background-color: #3d3d6b; -fx-border-color: #4a4a80; -fx-border-width: 0 0 2 0;");


    HBox leftSection = new HBox(16, turnInfo);
    leftSection.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(leftSection, Priority.ALWAYS);


    HBox centerSection = new HBox(16, rollButton, diceImageView);
    centerSection.setAlignment(Pos.CENTER);
    HBox.setHgrow(centerSection, Priority.ALWAYS);


    HBox rightSection = new HBox(12, buildMuteButton(), powerupBox);
    rightSection.setAlignment(Pos.CENTER_RIGHT);
    rightSection.setPadding(new Insets(0, 20, 0, 0));
    HBox.setHgrow(rightSection, Priority.ALWAYS);


    top.getChildren().addAll(leftSection, centerSection, rightSection);


   //HBox bottom = new HBox();
//bottom.setPrefHeight(120);
//bottom.setStyle("-fx-background-color: #2a2a4e;");
    // ───────────────────────────────────────────────────────────────────

    // replace your empty bottom HBox with this
HBox bottom = new HBox(20);
bottom.setPrefHeight(80);
bottom.setAlignment(Pos.CENTER_LEFT);
bottom.setPadding(new Insets(10, 20, 10, 20));
bottom.setStyle("-fx-background-color: #2a2a4e;");

ImageView cardIcon = new ImageView(
    new Image(getClass().getResourceAsStream("/game/images/card.png"))
);
cardIcon.setFitWidth(40);
cardIcon.setFitHeight(50);
cardIcon.setPreserveRatio(true);

Label cardTitle = new Label("CARD PILE");
cardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
cardTitle.setStyle("-fx-text-fill: #aaaaaa;");

cardsRemainingLabel = new Label(Board.getCards().size() + " cards left");
cardsRemainingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
cardsRemainingLabel.setStyle("-fx-text-fill: #ffcc00;");

reshuffledLabel = new Label("");
reshuffledLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
reshuffledLabel.setStyle("-fx-text-fill: #44ff88;");

VBox cardInfo = new VBox(3, cardTitle, cardsRemainingLabel, reshuffledLabel);
cardInfo.setAlignment(Pos.CENTER_LEFT);

bottom.getChildren().addAll(cardIcon, cardInfo);
    cellSize = Bindings.min(
        stage.widthProperty().subtract(240).divide(10),
        stage.heightProperty().subtract(240).divide(10)
    );


    VBox left = new VBox(10);
    left.prefWidthProperty().bind(
        stage.widthProperty().subtract(cellSize.multiply(10)).divide(2)
    );
    left.setAlignment(Pos.CENTER);
    left.setStyle("-fx-background-color: #2a2a4e;");
    left.setPadding(new Insets(10));


    VBox right = new VBox(10);
    right.prefWidthProperty().bind(
        stage.widthProperty().subtract(cellSize.multiply(10)).divide(2)
    );
    right.setAlignment(Pos.CENTER);
    right.setStyle("-fx-background-color: #2a2a4e;");
    right.setPadding(new Insets(10));


    VBox playerPanel = buildMonsterInfoPanel(
        controller.getPlayer(), "YOU", "#4fc3f7", playerPanelRefs, playerEnergyBar, stage
    );
    VBox opponentPanel = buildMonsterInfoPanel(
        controller.getOpponent(), "OPPONENT", "#ff6b6b", opponentPanelRefs, opponentEnergyBar, stage
    );
    playerPanel.setMaxWidth(170);
    opponentPanel.setMaxWidth(170);


    left.getChildren().add(playerPanel);
    right.getChildren().add(opponentPanel);


    BorderPane root = new BorderPane();
    root.setTop(top);
    root.setBottom(bottom);
    root.setRight(right);
    root.setLeft(left);
    root.setStyle("-fx-background-color: #1a1a2e;");


    for (int i = 0; i < Constants.BOARD_ROWS; i++) {
        for (int j = 0; j < Constants.BOARD_COLS; j++) {
            StackPane cell = new StackPane();
            cell.setStyle("-fx-background-color: #f5e6c8; -fx-border-color: #999;");
            cell.prefWidthProperty().bind(cellSize);
            cell.prefHeightProperty().bind(cellSize);
            board.add(cell, j, i);
        }
    }


    overlay = new Pane();
    overlay.setMouseTransparent(true);
    StackPane boardWithOverlay = new StackPane();
    boardWithOverlay.getChildren().addAll(board, overlay);
    root.setCenter(boardWithOverlay);
    controller.loadBoard(board, cellSize);


    playerToken = new ImageView(
    new Image(getClass().getResourceAsStream(
        getMonsterTokenPath(controller.getPlayer().getName())
    ))
    );
    playerToken.setFitWidth(40);
    playerToken.setFitHeight(40);
    playerToken.setPreserveRatio(true);
    playerToken.setMouseTransparent(true);
    playerToken.setStyle(
    "-fx-effect: dropshadow(gaussian, #4fc3f7, 8, 0.8, 0, 0);"
    );


  opponentToken = new ImageView(
    new Image(getClass().getResourceAsStream(
        getMonsterTokenPath(controller.getOpponent().getName())
    ))
    );
    opponentToken.setFitWidth(40);
    opponentToken.setFitHeight(40);
    opponentToken.setPreserveRatio(true);
    opponentToken.setMouseTransparent(true);
    opponentToken.setStyle(
        "-fx-effect: dropshadow(gaussian, #ff6b6b, 8, 0.8, 0, 0);"
    );


    overlay.getChildren().addAll(playerToken, opponentToken);


    placeTokenAtCell(playerToken, 0);
    placeTokenAtCell(opponentToken, 0);


    stage.widthProperty().addListener((obs, oldVal, newVal) -> {
        overlay.getChildren().clear();
        controller.drawTransports(overlay, board);
        overlay.getChildren().addAll(playerToken, opponentToken);
        placeTokenAtCell(playerToken,   playerTokenIndex);
        placeTokenAtCell(opponentToken, opponentTokenIndex);
    });


    stage.heightProperty().addListener((obs, oldVal, newVal) -> {
        overlay.getChildren().clear();
        controller.drawTransports(overlay, board);
        overlay.getChildren().addAll(playerToken, opponentToken);
        placeTokenAtCell(playerToken,   playerTokenIndex);
        placeTokenAtCell(opponentToken, opponentTokenIndex);
    });


    // ── ROLL BUTTON ACTION ──────────────────────────────────────────────
    rollButton.setOnAction(e -> {
        try {
            controller.playTurn();
            System.err.println("Cards after turn: " + Board.getCards().size());
            System.err.println("Card drawn: " + (Board.getLastCardDrawn() != null ? Board.getLastCardDrawn().getName() : "none"));
            int roll = controller.getLastRoll();
            rollButton.setDisable(true);


            Timeline diceAnimation = new Timeline();
            KeyFrame kf = new KeyFrame(Duration.millis(80), ev -> {
                int randomFace = (int)(Math.random() * 6) + 1;
                diceImageView.setImage(new Image(
                    getClass().getResourceAsStream("/game/images/dice" + randomFace + ".png")
                ));
            });
            diceAnimation.getKeyFrames().add(kf);
            diceAnimation.setCycleCount(15);


            diceAnimation.setOnFinished(ev -> {
                try {
                    diceImageView.setImage(new Image(
                        getClass().getResourceAsStream("/game/images/dice" + roll + ".png")
                    ));


                    currentMonsterLabel.setText(controller.getCurrentMonster().getName());


                    refreshMonsterPanels(
                        controller.getPlayer(), controller.getOpponent(),
                        playerPanelRefs, playerEnergyBar,
                        opponentPanelRefs, opponentEnergyBar
                    );
                    // update bottom team boxes
                   
                    int playerTarget   = controller.getPlayerPosition();
                    int opponentTarget = controller.getOpponentPosition();


                    animateTokenToCell(playerToken,   playerTarget,   null);
                    animateTokenToCell(opponentToken, opponentTarget, null);


                    playerTokenIndex   = playerTarget;
                    opponentTokenIndex = opponentTarget;


                    int maxSteps = Math.max(
                        Math.abs(playerTarget   - playerTokenIndex),
                        Math.abs(opponentTarget - opponentTokenIndex)
                    );
                    PauseTransition doorRefreshDelay = new PauseTransition(
                        Duration.millis(maxSteps * 120 + 300)
                    );
                    doorRefreshDelay.setOnFinished(eff ->
                        controller.refreshBoard(board, cellSize)
                    );
                    doorRefreshDelay.play();


                    Monster winner = controller.getWinner();
                    if (winner != null) {
                        showWinScreen(stage, winner);
                    }


                    Card drawnCard = controller.getLastCardDrawn();
                    

if (drawnCard != null) {
     controller.clearLastCardDrawn();
    showCardDrawnPopup(drawnCard);
    if (Board.getCards().size() == Board.getOriginalCards().size()) {
        reshuffledLabel.setText("🔀 Reshuffled!");
        PauseTransition clear = new PauseTransition(Duration.seconds(3));
        clear.setOnFinished(f -> reshuffledLabel.setText(""));
        clear.play();
    }
   
}
cardsRemainingLabel.setText(Board.getCards().size() + " cards left");


                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    rollButton.setDisable(false);
                }
            });


            diceAnimation.play();


        } catch (Exception ex) {
            showErrorAlert(ex.getMessage());
        }
    });
    // ───────────────────────────────────────────────────────────────────


    controller.drawTransports(overlay, board);
    stage.setScene(new Scene(root));
 stage.getScene().setOnKeyPressed(e -> {
   if (e.getCode() == javafx.scene.input.KeyCode.W) {
    controller.getCurrentMonster().setPosition(99);
    placeTokenAtCell(
        controller.getCurrentMonster() == controller.getPlayer() 
            ? playerToken : opponentToken, 
        99
    );
    playerTokenIndex = controller.getPlayerPosition();
    opponentTokenIndex = controller.getOpponentPosition();
    refreshMonsterPanels(
        controller.getPlayer(), controller.getOpponent(),
        playerPanelRefs, playerEnergyBar,
        opponentPanelRefs, opponentEnergyBar
    );
    // CHECK WINNER AFTER MOVING
   Monster winner = controller.getWinner();

if (winner != null) {
    showWinScreen(stage, winner);
}
    }
    if (e.getCode() == javafx.scene.input.KeyCode.E) {
        // increase current monster's energy by 200
        Monster current = controller.getCurrentMonster();
        current.setEnergy(current.getEnergy() + 200);
        refreshMonsterPanels(
            controller.getPlayer(), controller.getOpponent(),
            playerPanelRefs, playerEnergyBar,
            opponentPanelRefs, opponentEnergyBar
        );

         Monster winner = controller.getWinner();
        if (winner != null) {
            showWinScreen(stage, winner);
        }
    }
});

root.requestFocus(); 
    stage.setMaximized(true);
    stage.centerOnScreen();
}
//end game
    private String getTypeOfMonster(Monster monster) {
        String result = (monster instanceof Dasher) ? "Dasher"
                : (monster instanceof Dynamo)       ? "Dynamo"
                : (monster instanceof MultiTasker)  ? "MultiTasker"
                : (monster instanceof Schemer)      ? "Schemer" : "Unknown";
        return result;
    }


    private void showErrorAlert(String message) {
        Stage errorStage = new Stage();
        Label msg = new Label(message);
        msg.setWrapText(true);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        Button ok = new Button("OK");
        ok.setOnAction(e -> errorStage.close());
        VBox box = new VBox(20, msg, ok);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #1a1a2e;");
        errorStage.setScene(new Scene(box, 400, 200));
        errorStage.setTitle("Error");
        errorStage.show();
    }


    private VBox buildMonsterCard(Monster monster, String label,
            String bgColor, String accentColor) {


        Label playerLabel = new Label(label);
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        playerLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: " + accentColor + ";" +
            "-fx-padding: 4 16 4 16;" +
            "-fx-background-radius: 12;"
        );
       
        String imagePath = getMonsterImagePath(monster.getName());
        ImageView monsterImage = new ImageView(
            new Image(getClass().getResourceAsStream(imagePath))
        );
        monsterImage.setFitWidth(70);
        monsterImage.setFitHeight(60);
        monsterImage.setPreserveRatio(true);
       
        Label nameLabel = new Label(monster.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        nameLabel.setStyle("-fx-text-fill: " + accentColor + ";");
       
        Label typeLabel = new Label("Type: " + getTypeOfMonster(monster));
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        typeLabel.setStyle("-fx-text-fill: #eeeeee;");
       
        Label roleLabel = new Label("Role: " + monster.getOriginalRole().toString());
        roleLabel.setFont(Font.font("Arial", 13));
        roleLabel.setStyle("-fx-text-fill: #cccccc;");
       
        Label energyLabel = new Label("⚡ Starting Energy: " + monster.getEnergy());
        energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        energyLabel.setStyle("-fx-text-fill: #ffcc00;");
       
        Separator sep = new Separator();
       
        Label descTitle = new Label("Special Ability:");
        descTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        descTitle.setStyle("-fx-text-fill: " + accentColor + ";");
       
        Label descLabel = new Label(monster.getDescription());
        descLabel.setFont(Font.font("Arial", 12));
        descLabel.setStyle("-fx-text-fill: #dddddd;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(230);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
       
        VBox card = new VBox(10,
            playerLabel, monsterImage, nameLabel,
            typeLabel, roleLabel, energyLabel,
            sep, descTitle, descLabel
        );
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setPrefWidth(280);
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;"
        );
        // After the existing card VBox is built, append team section
Separator teamSep = new Separator();

Label teamTitle = new Label("TEAM ON BOARD");
teamTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
teamTitle.setStyle("-fx-text-fill: #aaaaaa;");

VBox teamBox = new VBox(4, teamSep, teamTitle);

for (Monster m : Board.getStationedMonsters()) {
    if (m.getRole() == monster.getRole()) {
        Label mLabel = new Label(m.getName() + " · ⚡" + m.getEnergy() + " · cell " + m.getPosition());
        mLabel.setFont(Font.font("Arial", 10));
        mLabel.setStyle("-fx-text-fill: #cccccc; -fx-background-color: #1a1a2e; -fx-padding: 4 8 4 8; -fx-background-radius: 6;");
        mLabel.setWrapText(true);
        mLabel.setMaxWidth(150);
        teamBox.getChildren().add(mLabel);
    }
}
if (label.equals("YOU")) {
    playerTeamBox = teamBox;
} else {
    opponentTeamBox = teamBox;
}


card.getChildren().add(teamBox);
        return card;
    }


    private String getMonsterImagePath(String name) {
        switch (name) {
            case "James P. Sullivan":   return "/game/images/james_sullivanID.png";
            case "Mike Wazowski":       return "/game/images/mike_wazowskiID.png";
            case "Randall Boggs":       return "/game/images/Randall_BoggsID.jpg";
            case "Celia Mae":           return "/game/images/celia_maeID.jpg";
            case "Fungus":              return "/game/images/fungus2.png";
            case "Yeti":                return "/game/images/yetiID.png";
            default:                    return "/game/images/green_guy.jpeg";
        }
    }


    public void showMonsterRevealScreen(Stage stage) {
        stage.setTitle("Door Dash – Your Monster");
     
        Monster player   = controller.getPlayer();
        Monster opponent = controller.getOpponent();
     
        Label title = new Label("THE COMPETITORS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setStyle("-fx-text-fill: white;");
     
        Label subtitle = new Label("The Floor awaits. Only one monster will emerge victorious.");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: #aaaaaa;");
     
        VBox playerCard   = buildMonsterCard(player,   "YOU",      "#1a3a5c", "#4fc3f7");
        VBox opponentCard = buildMonsterCard(opponent,  "OPPONENT", "#3a1a1a", "#ff6b6b");
     
        Label vsLabel = new Label("VS");
        vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        vsLabel.setStyle("-fx-text-fill: #ffcc00;");
     
        HBox cardsRow = new HBox(40, playerCard, vsLabel, opponentCard);
        cardsRow.setAlignment(Pos.CENTER);
     
        Button startButton = new Button("▶  START GAME");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        startButton.setStyle(
            "-fx-background-color: #ff6600;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 14 40 14 40;" +
            "-fx-background-radius: 30;"
        );
        startButton.setOnAction(e -> Game(stage));
     
        Button backButton = new Button("← Change Side");
        backButton.setFont(Font.font("Arial", 14));
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-border-color: #aaaaaa;" +
            "-fx-border-radius: 20;" +
            "-fx-padding: 8 24 8 24;"
        );
        backButton.setOnAction(e -> showTeamSelectScreen(stage));
     
        VBox root = new VBox(28, title, subtitle, cardsRow, startButton, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a2e;");
     
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.centerOnScreen();
    }


    private void showInfoAlert(String message) {
        Stage infoStage = new Stage();
        Label msg = new Label(message);
        msg.setWrapText(true);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        Button ok = new Button("OK");
        ok.setOnAction(e -> infoStage.close());
        VBox box = new VBox(20, msg, ok);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #1a1a2e;");
        infoStage.setScene(new Scene(box, 400, 200));
        infoStage.setTitle("Info");
        infoStage.show();
    }

    //
    public void showWinScreen(Stage stage, Monster winner) {
    playMusic("/game/audio/win.mp3");

    Monster player   = controller.getPlayer();
    Monster opponent = controller.getOpponent();
    boolean playerWon = winner.getName().equals(player.getName());

    // --- Fireworks canvas ---
    javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(1600, 900);
    javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
    java.util.List<double[]> particles = new java.util.ArrayList<>();
    Random rng = new Random();

    Timeline fireworks = new Timeline(new KeyFrame(Duration.millis(16), e -> {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (rng.nextInt(3) == 0) {
            double bx = 100 + rng.nextDouble() * 1400;
            double by = 100 + rng.nextDouble() * 400;
            double r = rng.nextDouble(), g = rng.nextDouble(), b = rng.nextDouble();
            for (int p = 0; p < 60; p++) {
                double angle = rng.nextDouble() * Math.PI * 2;
                double speed = 1.5 + rng.nextDouble() * 5;
                double life  = 40 + rng.nextInt(40);
                particles.add(new double[]{bx, by,
                    Math.cos(angle) * speed, Math.sin(angle) * speed,
                    life, life, r, g, b});
            }
        }
        java.util.Iterator<double[]> it = particles.iterator();
        while (it.hasNext()) {
            double[] p = it.next();
            p[0] += p[2]; p[1] += p[3]; p[3] += 0.08; p[4] -= 1;
            if (p[4] <= 0) { it.remove(); continue; }
            double alpha = p[4] / p[5];
            gc.setFill(javafx.scene.paint.Color.color(p[6], p[7], p[8], alpha));
            double size = 3 * alpha + 1;
            gc.fillOval(p[0] - size/2, p[1] - size/2, size, size);
        }
    }));
    fireworks.setCycleCount(Timeline.INDEFINITE);
    fireworks.play();

    // --- Game Won / Game Over banner ---
    Label bannerLabel = new Label(playerWon ? "🎉 GAME WON!" : "💀 GAME OVER");
    bannerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 52));
    bannerLabel.setStyle("-fx-text-fill: " + (playerWon ? "#ffcc00" : "#ff4444") + ";");
    javafx.scene.effect.DropShadow textGlow = new javafx.scene.effect.DropShadow();
    textGlow.setColor(playerWon
        ? javafx.scene.paint.Color.web("#ffcc00")
        : javafx.scene.paint.Color.web("#ff4444"));
    textGlow.setRadius(20);
    bannerLabel.setEffect(textGlow);
    Timeline textPulse = new Timeline(
        new KeyFrame(Duration.ZERO,         new KeyValue(textGlow.radiusProperty(), 10)),
        new KeyFrame(Duration.seconds(0.8), new KeyValue(textGlow.radiusProperty(), 35))
    );
    textPulse.setAutoReverse(true);
    textPulse.setCycleCount(Timeline.INDEFINITE);
    textPulse.play();

    // --- Winner announcement ---
    Label crownLabel = new Label("👑");
    crownLabel.setFont(Font.font("Arial", 50));

    ImageView winnerImage = new ImageView(
        new Image(getClass().getResourceAsStream(getMonsterImagePath(winner.getName())))
    );
    winnerImage.setFitWidth(180);
    winnerImage.setFitHeight(160);
    winnerImage.setPreserveRatio(true);
    TranslateTransition bounce = new TranslateTransition(Duration.millis(600), winnerImage);
    bounce.setByY(-18);
    bounce.setAutoReverse(true);
    bounce.setCycleCount(Timeline.INDEFINITE);
    bounce.play();

    Label winnerName = new Label(winner.getName());
    winnerName.setFont(Font.font("Arial", FontWeight.BOLD, 30));
    winnerName.setStyle("-fx-text-fill: white;");

    Label winnerRole = new Label("Role: " + winner.getRole().toString());
    winnerRole.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    winnerRole.setStyle(
        "-fx-text-fill: #1a1a2e;" +
        "-fx-background-color: " + (playerWon ? "#4fc3f7" : "#ff6b6b") + ";" +
        "-fx-padding: 4 16 4 16;" +
        "-fx-background-radius: 12;"
    );

    VBox winnerBox = new VBox(8, crownLabel, winnerImage, winnerName, winnerRole);
    winnerBox.setAlignment(Pos.CENTER);
    winnerBox.setPadding(new Insets(16));
    winnerBox.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-background-radius: 16;" +
        "-fx-border-color: " + (playerWon ? "#ffcc00" : "#ff4444") + ";" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 16;"
    );

    // --- Final energy of both monsters ---
    VBox playerEnergyBox = buildFinalEnergyCard(player, "YOU",      "#4fc3f7", winner);
    VBox opponentEnergyBox = buildFinalEnergyCard(opponent, "OPPONENT", "#ff6b6b", winner);

    HBox energyRow = new HBox(24, playerEnergyBox, opponentEnergyBox);
    energyRow.setAlignment(Pos.CENTER);

    // --- Return to start button ---
    Button playAgainBtn = new Button("🔄  PLAY AGAIN");
    playAgainBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    playAgainBtn.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 12 32 12 32;" +
        "-fx-background-radius: 28;" +
        "-fx-cursor: hand;"
    );
    playAgainBtn.setOnAction(e -> {
        fireworks.stop();
        this.controller = new GameController(this);
        WelcomeStage(stage);
    });

    VBox content = new VBox(18,
        bannerLabel, winnerBox, energyRow, playAgainBtn
    );
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(40));

    StackPane root = new StackPane(canvas, content);
    root.setStyle("-fx-background-color: #1a1a2e;");
    canvas.widthProperty().bind(stage.widthProperty());
    canvas.heightProperty().bind(stage.heightProperty());

    stage.setScene(new Scene(root));
    stage.setMaximized(true);
    stage.centerOnScreen();
}

// Helper to build a final energy card for each monster
private VBox buildFinalEnergyCard(Monster monster, String label, String accentColor, Monster winner) {
    boolean isWinner = monster.getName().equals(winner.getName());

    Label badge = new Label((isWinner ? "👑 " : "") + label);
    badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    badge.setStyle(
        "-fx-text-fill: #1a1a2e;" +
        "-fx-background-color: " + accentColor + ";" +
        "-fx-padding: 3 12 3 12;" +
        "-fx-background-radius: 10;"
    );

    ImageView img = new ImageView(
        new Image(getClass().getResourceAsStream(getMonsterImagePath(monster.getName())))
    );
    img.setFitWidth(70);
    img.setFitHeight(60);
    img.setPreserveRatio(true);

    Label nameLabel = new Label(monster.getName());
    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
    nameLabel.setStyle("-fx-text-fill: " + accentColor + ";");

    Label roleLabel = new Label("Role: " + monster.getRole().toString());
    roleLabel.setFont(Font.font("Arial", 12));
    roleLabel.setStyle("-fx-text-fill: #cccccc;");

    Label energyLabel = new Label("⚡ Final Energy: " + monster.getEnergy());
    energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    energyLabel.setStyle("-fx-text-fill: #ffcc00;");

    VBox card = new VBox(8, badge, img, nameLabel, roleLabel, energyLabel);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(16));
    card.setPrefWidth(180);
    card.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-background-radius: 14;" +
        "-fx-border-color: " + accentColor + ";" +
        "-fx-border-width: " + (isWinner ? "3" : "1") + ";" +
        "-fx-border-radius: 14;"
    );
    return card;
}
    

    public void showCardDrawnPopup(Card card) {
        Stage popup = new Stage();
        popup.initModality(Modality.NONE);


        String iconPath = getCardIconPath(card);
        ImageView icon = new ImageView(
            new Image(getClass().getResourceAsStream(iconPath))
        );
        icon.setFitWidth(80);
        icon.setFitHeight(80);
        icon.setPreserveRatio(true);


        Label header = new Label("🃏  CARD DRAWN");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        header.setStyle("-fx-text-fill: #aaaaaa;");


        Label nameLabel = new Label(card.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        nameLabel.setStyle("-fx-text-fill: #ffcc00;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);


        Label effectLabel = new Label(card.getDescription());
        effectLabel.setFont(Font.font("Arial", 15));
        effectLabel.setStyle("-fx-text-fill: #dddddd;");
        effectLabel.setWrapText(true);
        effectLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        effectLabel.setMaxWidth(260);


        Label luckyLabel = new Label(card.isLucky() ? "✨ Lucky!" : "💀 Unlucky!");
        luckyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        luckyLabel.setStyle(card.isLucky()
            ? "-fx-text-fill: #44ff88; -fx-background-color: #1a3a2a; -fx-padding: 4 12 4 12; -fx-background-radius: 10;"
            : "-fx-text-fill: #ff4444; -fx-background-color: #3a1a1a; -fx-padding: 4 12 4 12; -fx-background-radius: 10;"
        );


        Button continueBtn = new Button("CONTINUE  ▶");
        continueBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        continueBtn.setStyle(
            "-fx-background-color: #ff6600;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 28 10 28;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;"
        );
        continueBtn.setOnAction(e -> popup.close());


        VBox content = new VBox(14, header, icon, nameLabel, effectLabel, luckyLabel, continueBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(36, 40, 36, 40));
        content.setStyle(
            "-fx-background-color: #2a2a4e;" +
            "-fx-border-color: #ffcc00;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;"
        );


        popup.setScene(new Scene(content, 340, 420));
        popup.setTitle("Card Drawn");


        content.setTranslateY(300);
        popup.show();
        popup.toFront();
        Timeline slideUp = new Timeline(
            new KeyFrame(Duration.millis(300),
                new KeyValue(content.translateYProperty(), 0,
                    javafx.animation.Interpolator.EASE_OUT))
        );
        slideUp.play();
    }


    private String getCardIconPath(Card card) {
        if (card instanceof SwapperCard)     return "/game/images/position_swap .png";
        if (card instanceof ShieldCard)      return "/game/images/sheild_icon.png";
        if (card instanceof EnergyStealCard) return "/game/images/small_thief.png";
        if (card instanceof StartOverCard)   return "/game/images/square_one.png";
        if (card instanceof ConfusionCard)   return "/game/images/confused_icon.png";
        return null;
    }


    private VBox buildMonsterInfoPanel(Monster monster, String label,
            String accentColor, Label[] refs, ProgressBar energyBar, Stage stage) {
 
        Label badge = new Label(label);
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        badge.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: " + accentColor + ";" +
            "-fx-padding: 3 12 3 12;" +
            "-fx-background-radius: 10;"
        );
 
        String imagePath = getMonsterImagePath(monster.getName());
        ImageView monsterImage = new ImageView(
            new Image(getClass().getResourceAsStream(imagePath))
        );
        monsterImage.setFitWidth(100);
        monsterImage.setFitHeight(80);
        monsterImage.setPreserveRatio(true);
 
        Label nameLabel = new Label(monster.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-text-fill: " + accentColor + ";");
 
        Label typeLabel = new Label(getTypeOfMonster(monster));
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        typeLabel.setStyle(
            "-fx-text-fill: #1a1a2e;" +
            "-fx-background-color: " + accentColor + ";" +
            "-fx-padding: 2 10 2 10;" +
            "-fx-background-radius: 8;"
        );
 
        Label originalRoleLabel = new Label("Role: " + monster.getOriginalRole());
        originalRoleLabel.setFont(Font.font("Arial", 11));
        originalRoleLabel.setStyle("-fx-text-fill: #aaaaaa;");
 
        Label currentRoleLabel = new Label("Current: " + monster.getRole());
        currentRoleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        currentRoleLabel.setStyle("-fx-text-fill: white;");
        refs[2] = currentRoleLabel;
 
        Label positionLabel = new Label("📍 Position: " + monster.getPosition());
        positionLabel.setFont(Font.font("Arial", 11));
        positionLabel.setStyle("-fx-text-fill: #cccccc;");
        refs[1] = positionLabel;
 
        Label energyLabel = new Label("⚡ " + monster.getEnergy() + " / 1000");
        energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        energyLabel.setStyle("-fx-text-fill: #ffcc00;");
        refs[0] = energyLabel;
 
        double energyPercent = Math.min((double) monster.getEnergy() / 1000.0, 1.0);
        energyBar.setProgress(energyPercent);
        energyBar.prefWidthProperty().bind(stage.widthProperty().divide(8));
        energyBar.setStyle(getEnergyBarStyle(energyPercent));
 
        VBox energySection = new VBox(4, energyLabel, energyBar);
        energySection.setAlignment(Pos.CENTER);
 
        Label shieldLabel = new Label();
        shieldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        refs[3] = shieldLabel;
 
        Label confusionLabel = new Label();
        confusionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        refs[4] = confusionLabel;
 
        Label specialLabel = new Label();
        specialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        refs[5] = specialLabel;
 
        VBox statusBox = new VBox(3, shieldLabel, confusionLabel, specialLabel);
        statusBox.setAlignment(Pos.CENTER);
 
        refreshStatusLabels(monster, refs);
 
        Separator sep = new Separator();
 
        VBox card = new VBox(8,
            badge, monsterImage, nameLabel, typeLabel,
            originalRoleLabel, currentRoleLabel,
            sep, positionLabel, energySection, statusBox
        );
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: #2a2a4e;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 16;"
        );
        // --- TEAM ON BOARD SECTION ---
Separator teamSep = new Separator();

Label teamTitle = new Label("TEAM ON BOARD");
teamTitle.setFont(Font.font("Arial", FontWeight.BOLD, 10));
teamTitle.setStyle("-fx-text-fill: #aaaaaa;");

VBox teamBox = new VBox(4, teamSep, teamTitle);

for (Monster m : Board.getStationedMonsters()) {
    if (m.getRole() == monster.getRole()) {
        Label mLabel = new Label(m.getName() + " · ⚡" + m.getEnergy() + " · cell " + m.getPosition());
        mLabel.setFont(Font.font("Arial", 10));
        mLabel.setStyle("-fx-text-fill: #cccccc; -fx-background-color: #1a1a2e; -fx-padding: 4 8 4 8; -fx-background-radius: 6;");
        mLabel.setWrapText(true);
        mLabel.setMaxWidth(150);
        teamBox.getChildren().add(mLabel);
    }
}
if (label.equals("YOU")) {
    playerTeamBox = teamBox;
} else {
    opponentTeamBox = teamBox;
}
card.getChildren().add(teamBox);
        return card;
    }


    private String getEnergyBarStyle(double percent) {
        String color;
        if (percent > 0.6)      color = "#44ff88";
        else if (percent > 0.3) color = "#ffcc00";
        else                    color = "#ff4444";
 
        return "-fx-accent: " + color + ";" +
               "-fx-background-color: #1a1a2e;" +
               "-fx-background-radius: 6;" +
               "-fx-pref-height: 12;";
    }


    private void refreshStatusLabels(Monster monster, Label[] refs) {
        if (monster.isShielded()) {
            refs[3].setText("🛡 Shielded");
            refs[3].setStyle("-fx-text-fill: #4fc3f7; -fx-background-color: #1a3a5c; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
        } else {
            refs[3].setText("");
            refs[3].setStyle("");
        }
 
        if (monster.isConfused()) {
            refs[4].setText("😵 Confused (" + monster.getConfusionTurns() + ")");
            refs[4].setStyle("-fx-text-fill: #ff44ff; -fx-background-color: #2a1a3a; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
        } else {
            refs[4].setText("");
            refs[4].setStyle("");
        }
 
        if (monster.isFrozen()) {
            refs[5].setText("🧊 Frozen");
            refs[5].setStyle("-fx-text-fill: #88ddff; -fx-background-color: #1a2a3a; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
        } else if (monster instanceof Dasher && ((Dasher) monster).getMomentumTurns() > 0) {
            refs[5].setText("💨 Momentum (" + ((Dasher) monster).getMomentumTurns() + ")");
            refs[5].setStyle("-fx-text-fill: #ffaa44; -fx-background-color: #2a1a00; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
        } else if (monster instanceof MultiTasker && ((MultiTasker) monster).getNormalSpeedTurns() > 0) {
            refs[5].setText("🎯 Focus (" + ((MultiTasker) monster).getNormalSpeedTurns() + ")");
            refs[5].setStyle("-fx-text-fill: #44ffaa; -fx-background-color: #002a1a; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
        } else {
            refs[5].setText("");
            refs[5].setStyle("");
        }
    }


    private void refreshMonsterPanels(Monster player, Monster opponent,
            Label[] playerRefs, ProgressBar playerBar,
            Label[] opponentRefs, ProgressBar opponentBar) {
                boolean playerWasShielded = "🛡 Shielded".equals(playerRefs[3].getText());
                boolean opponentWasShielded = "🛡 Shielded".equals(opponentRefs[3].getText());
 
        double playerPercent = Math.min((double) player.getEnergy() / 1000.0, 1.0);
        playerRefs[0].setText("⚡ " + player.getEnergy() + " / 1000");
        playerRefs[1].setText("📍 Position: " + player.getPosition());
        playerRefs[2].setText("Current: " + player.getRole());
        playerRefs[2].setStyle(player.isConfused()
            ? "-fx-text-fill: #ff44ff; -fx-font-weight: bold;"
            : "-fx-text-fill: white; -fx-font-weight: bold;");
        playerBar.setProgress(playerPercent);
        playerBar.setStyle(getEnergyBarStyle(playerPercent));
        refreshStatusLabels(player, playerRefs);
 
        double opponentPercent = Math.min((double) opponent.getEnergy() / 1000.0, 1.0);
        opponentRefs[0].setText("⚡ " + opponent.getEnergy() + " / 1000");
        opponentRefs[1].setText("📍 Position: " + opponent.getPosition());
        opponentRefs[2].setText("Current: " + opponent.getRole());
        opponentRefs[2].setStyle(opponent.isConfused()
            ? "-fx-text-fill: #ff44ff; -fx-font-weight: bold;"
            : "-fx-text-fill: white; -fx-font-weight: bold;");
        opponentBar.setProgress(opponentPercent);
        opponentBar.setStyle(getEnergyBarStyle(opponentPercent));
        refreshStatusLabels(opponent, opponentRefs);
        refreshTeamBox(playerTeamBox, player);
        refreshTeamBox(opponentTeamBox, opponent);
        if (playerWasShielded && !player.isShielded()) {
            showShieldBrokenPopup(player.getName());
        }
    if (opponentWasShielded && !opponent.isShielded()) {
        showShieldBrokenPopup(opponent.getName());
        }
    }


  private void playMusic(String audioPath) {
    if (currentMusic != null) {
        currentMusic.stop();
    }
    java.net.URL resource = getClass().getResource(audioPath);
    if (resource == null) return;
    javafx.scene.media.Media media = new javafx.scene.media.Media(resource.toString());
    currentMusic = new javafx.scene.media.MediaPlayer(media);
    currentMusic.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE);
    currentMusic.setMute(isMuted);   // ← respects current mute state
    currentMusic.play();
}


    private void placeTokenAtCell(ImageView token, int cellIndex) {
        javafx.scene.Node cell = controller.getNodeFromGrid(board, cellIndex);
        if (cell == null) return;


        Platform.runLater(() -> {
            Bounds cellBounds   = cell.localToScene(cell.getBoundsInLocal());
            Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());


            double centerX = (cellBounds.getMinX() + cellBounds.getMaxX()) / 2
                             - overlayBounds.getMinX();
            double centerY = (cellBounds.getMinY() + cellBounds.getMaxY()) / 2
                             - overlayBounds.getMinY();


            token.setX(centerX - token.getFitWidth()  / 2);
            token.setY(centerY - token.getFitHeight() / 2);
        });
    }


    // ← UPDATED: uses tracked index fields instead of pixel-based lookup
    private void animateTokenToCell(ImageView token, int targetIndex, Runnable onFinished) {
        int currentIndex = (token == playerToken) ? playerTokenIndex : opponentTokenIndex;


        if (currentIndex == targetIndex) {
            if (onFinished != null) onFinished.run();
            return;
        }


        int step = (targetIndex > currentIndex) ? 1 : -1;
        ArrayList<Integer> steps = new ArrayList<>();
        for (int i = currentIndex + step; i != targetIndex + step; i += step) {
            steps.add(i);
        }


        animateSteps(token, steps, 0, onFinished);
    }


    private void animateSteps(ImageView token, ArrayList<Integer> steps, int stepIndex, Runnable onFinished) {
        if (stepIndex >= steps.size()) {
            if (onFinished != null) onFinished.run();
            return;
        }


        int cellIndex = steps.get(stepIndex);
        javafx.scene.Node cell = controller.getNodeFromGrid(board, cellIndex);
        if (cell == null) {
            animateSteps(token, steps, stepIndex + 1, onFinished);
            return;
        }


        Platform.runLater(() -> {
            Bounds cellBounds    = cell.localToScene(cell.getBoundsInLocal());
            Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());


            double targetX = (cellBounds.getMinX() + cellBounds.getMaxX()) / 2
                             - overlayBounds.getMinX() - token.getFitWidth()  / 2;
            double targetY = (cellBounds.getMinY() + cellBounds.getMaxY()) / 2
                             - overlayBounds.getMinY() - token.getFitHeight() / 2;


            Timeline move = new Timeline(
                new KeyFrame(Duration.millis(120),
                    new KeyValue(token.xProperty(), targetX, javafx.animation.Interpolator.EASE_BOTH),
                    new KeyValue(token.yProperty(), targetY, javafx.animation.Interpolator.EASE_BOTH)
                )
            );


            move.setOnFinished(e ->
                animateSteps(token, steps, stepIndex + 1, onFinished)
            );


            move.play();
        });
    }
    private void refreshTeamBox(VBox teamBox, Monster monster) {
    // keep first 2 children (separator + title), remove old labels
    while (teamBox.getChildren().size() > 2) {
        teamBox.getChildren().remove(2);
    }
    for (Monster m : Board.getStationedMonsters()) {
        if (m.getRole() == monster.getRole()) {
            Label mLabel = new Label(m.getName() + " · ⚡" + m.getEnergy() + " · cell " + m.getPosition());
            mLabel.setFont(Font.font("Arial", 10));
            mLabel.setStyle("-fx-text-fill: #cccccc; -fx-background-color: #1a1a2e; -fx-padding: 4 8 4 8; -fx-background-radius: 6;");
            mLabel.setWrapText(true);
            mLabel.setMaxWidth(150);
            teamBox.getChildren().add(mLabel);
        }
    }
}
private String getMonsterTokenPath(String name) {
    switch (name) {
        case "James P. Sullivan":   return "/game/images/James_P.Sullivan.png";
        case "Mike Wazowski":       return "/game/images/Mike_Wazowski.png";
        case "Randall Boggs":       return "/game/images/Randall_Boggs.png";
        case "Celia Mae":           return "/game/images/Celia_Mae.png";
        case "Fungus":              return "/game/images/Fungus.png";
        case "Yeti":                return "/game/images/Yeti.png";
        default:                    return "/game/images/Mike_Wazowski.png";
    }
}
private void showShieldBrokenPopup(String monsterName) {
    Stage popup = new Stage();
    popup.initModality(Modality.NONE);

    ImageView icon = new ImageView(
        new Image(getClass().getResourceAsStream("/game/images/sheild_icon.png"))
    );
    icon.setFitWidth(80);
    icon.setFitHeight(80);
    icon.setPreserveRatio(true);

    Label header = new Label("🛡  SHIELD BROKEN");
    header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    header.setStyle("-fx-text-fill: #aaaaaa;");

    Label nameLabel = new Label(monsterName);
    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
    nameLabel.setStyle("-fx-text-fill: #4fc3f7;");

    Label effectLabel = new Label("The shield absorbed the hit\nbut has now been destroyed!");
    effectLabel.setFont(Font.font("Arial", 15));
    effectLabel.setStyle("-fx-text-fill: #dddddd;");
    effectLabel.setWrapText(true);
    effectLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    effectLabel.setMaxWidth(260);

    Button continueBtn = new Button("CONTINUE  ▶");
    continueBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    continueBtn.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 10 28 10 28;" +
        "-fx-background-radius: 20;" +
        "-fx-cursor: hand;"
    );
    continueBtn.setOnAction(e -> popup.close());

    VBox content = new VBox(14, header, icon, nameLabel, effectLabel, continueBtn);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(36, 40, 36, 40));
    content.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-border-color: #4fc3f7;" +
        "-fx-border-width: 3;" +
        "-fx-border-radius: 20;" +
        "-fx-background-radius: 20;"
    );

    popup.setScene(new Scene(content, 340, 380));
    popup.setTitle("Shield Broken");

    content.setTranslateY(300);
    popup.show();
    popup.toFront();
    Timeline slideUp = new Timeline(
        new KeyFrame(Duration.millis(300),
            new KeyValue(content.translateYProperty(), 0,
                javafx.animation.Interpolator.EASE_OUT))
    );
    slideUp.play();
}
// Add this method anywhere in Main.java (e.g. after showTeamSelectScreen)

private void showHowToPlay(Stage stage) {
    Stage popup = new Stage();
    popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
    popup.initOwner(stage);
    popup.setTitle("How to Play");

    // ── Title ────────────────────────────────────────────────
    Label title = new Label("❓  HOW TO PLAY");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    title.setStyle("-fx-text-fill: #ffcc00;");

    // ── Sections ─────────────────────────────────────────────
    String[][] sections = {
        { "🎯  GOAL",
          "Be the first monster to reach cell 99 (Boo's Door) with at least 1000 energy." },
        { "🎲  YOUR TURN",
          "1. (Optional) Use your powerup — costs 500 energy.\n"
        + "2. Roll the dice (1–6) and move forward.\n"
        + "3. Apply the effect of the cell you land on.\n"
        + "4. If you land on the opponent's cell, move is cancelled — roll again." },
        { "🚪  DOOR CELLS",
          "Same role → you and your whole team GAIN the door's energy.\n"
        + "Different role → you and your team LOSE the door's energy.\n"
        + "A shield blocks the loss (door stays closed for next time)." },
        { "🃏  CARD CELLS",
          "Draw a random card:\n"
        + "• Position Swap  • Energy Steal  • Shield  • Start Over  • Confusion" },
        { "🧦  CONTAMINATION SOCKS",
          "Move backward by the cell's value and lose 100 energy.\n"
        + "Shield blocks the energy loss, but you still move back." },
        { "🏭  CONVEYOR BELTS",
          "Move forward by the cell's value — free ride!" },
        { "👾  MONSTER CELLS",
          "Same role → activate your powerup for FREE.\n"
        + "Different role → if you have more energy than the cell monster, swap energies." },
        { "⚡  MONSTER TYPES",
          "Dasher      — 2× speed;  powerup: 3× for 3 turns.\n"
        + "Dynamo      — all energy changes doubled;  powerup: freeze opponent 1 turn.\n"
        + "MultiTasker — ½ speed, +200 energy bonus;  powerup: normal speed 2 turns.\n"
        + "Schemer     — +10 on every energy change;  powerup: steal 10 from all." },
    };

    VBox sectionsBox = new VBox(8);
    for (int i = 0; i < sections.length; i++) {
        if (i != 0) sectionsBox.getChildren().add(new Separator());

        Label header = new Label(sections[i][0]);
        header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        header.setStyle("-fx-text-fill: #ffcc00;");

        Label body = new Label(sections[i][1]);
        body.setFont(Font.font("Arial", 12));
        body.setStyle(
            "-fx-text-fill: #dddddd;" +
            "-fx-background-color: #1e1e3a;" +
            "-fx-padding: 8 12 8 12;" +
            "-fx-background-radius: 8;"
        );
        body.setWrapText(true);
        body.setMaxWidth(520);

        sectionsBox.getChildren().addAll(header, body);
    }

    ScrollPane scroll = new ScrollPane(sectionsBox);
    scroll.setFitToWidth(true);
    scroll.setPrefHeight(420);
    scroll.setStyle("-fx-background: #2a2a4e; -fx-background-color: #2a2a4e;");
    scroll.setPadding(new Insets(0, 4, 0, 0));

    // ── Close button (centered) ───────────────────────────────
    Button closeBtn = new Button("✖  CLOSE");
    closeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
    closeBtn.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 9 28 9 28;" +
        "-fx-background-radius: 20;" +
        "-fx-cursor: hand;"
    );
    closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
        "-fx-background-color: #ff8833;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 9 28 9 28;" +
        "-fx-background-radius: 20;" +
        "-fx-cursor: hand;"
    ));
    closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 9 28 9 28;" +
        "-fx-background-radius: 20;" +
        "-fx-cursor: hand;"
    ));
    closeBtn.setOnAction(e -> popup.close());

    HBox btnRow = new HBox(closeBtn);
    btnRow.setAlignment(Pos.CENTER);

    // ── Assemble ──────────────────────────────────────────────
    VBox content = new VBox(16, title, scroll, btnRow);
    content.setAlignment(Pos.TOP_CENTER);
    content.setPadding(new Insets(32, 32, 28, 32));
    content.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-background-radius: 20;" +
        "-fx-border-color: #ffcc00;" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 20;"
    );
    content.setMaxWidth(600);

    // slide-down entrance
    content.setTranslateY(-40);
    content.setOpacity(0);

    StackPane root = new StackPane(content);
    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color: rgba(10,10,30,0.85);");
    root.setPadding(new Insets(40));

    popup.setScene(new Scene(root, 660, 600));
    popup.show();
    popup.centerOnScreen();

    Timeline entrance = new Timeline(
        new KeyFrame(Duration.millis(250),
            new KeyValue(content.translateYProperty(), 0,   javafx.animation.Interpolator.EASE_OUT),
            new KeyValue(content.opacityProperty(),    1.0, javafx.animation.Interpolator.EASE_OUT)
        )
    );
    entrance.play();
}
private Button buildMuteButton() {
    Button muteBtn = new Button(isMuted ? "🔇" : "🔊");
    muteBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    muteBtn.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 8 14 8 14;" +
        "-fx-background-radius: 20;" +
        "-fx-border-color: #555577;" +
        "-fx-border-width: 1;" +
        "-fx-border-radius: 20;" +
        "-fx-cursor: hand;"
    );
    muteBtn.setOnMouseEntered(e -> muteBtn.setStyle(
        "-fx-background-color: #3a3a6e;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 8 14 8 14;" +
        "-fx-background-radius: 20;" +
        "-fx-border-color: #aaaaaa;" +
        "-fx-border-width: 1;" +
        "-fx-border-radius: 20;" +
        "-fx-cursor: hand;"
    ));
    muteBtn.setOnMouseExited(e -> muteBtn.setStyle(
        "-fx-background-color: #2a2a4e;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 8 14 8 14;" +
        "-fx-background-radius: 20;" +
        "-fx-border-color: #555577;" +
        "-fx-border-width: 1;" +
        "-fx-border-radius: 20;" +
        "-fx-cursor: hand;"
    ));
    muteBtn.setOnAction(e -> {
        isMuted = !isMuted;
        muteBtn.setText(isMuted ? "🔇" : "🔊");
        if (currentMusic != null) {
            currentMusic.setMute(isMuted);
        }
    });
    return muteBtn;
}

    public static void main(String[] args) {
        launch(args);
    }
}



