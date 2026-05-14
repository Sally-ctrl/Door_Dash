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
    public void start(Stage stage){
    	//i updated the constructor of game controller as well to create the game controller as soon as the view is launched 
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

        Label title = new Label("Door Dash");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setStyle("-fx-text-fill: #140a04;");

        Label description = new Label(
            "The goal:\n" +
            "Be the first monster to reach cell 99 (Boo's Door) with at least 1000 energy\n\n" +
            "How to Win\n" +
            "You win if you:\n" +
            "Reach cell 99, AND\n" +
            "Have 1000 or more energy\n\n" +
            "On Your Turn:\n\n" +
            "(Optional) Use your monster powerup (costs 500 energy if used manually).\n\n" +
            "Roll a 6-sided dice (1-6).\n" +
            "Move forward that number of cells.\n" +
            "Apply the effect of the cell you land on:\n" +
            "- Doors -> gain or lose energy for your whole team\n" +
            "- Cards -> draw a random card effect\n" +
            "- Monster cells -> trigger special monster interactions\n" +
            "- Conveyor/Socks -> move forward/backward with effects\n" +
            "- Normal cell -> nothing happens\n\n" +
            "If you land on the opponent's cell -> your move is cancelled and you retry."
        );
        description.setWrapText(true);
        description.setFont(Font.font("Arial", 16));
        description.setStyle("-fx-text-fill: #021b5e;");
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #bb69e1;");

        Button playButton = new Button("PLAY");
        playButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        playButton.setStyle("-fx-background-color: #ff6600; -fx-text-fill: white; -fx-padding: 14 40 14 40; -fx-background-radius: 30;");
        
        //changed this to e -> showTeam bc from showTeam it will go to game stage 
        playButton.setOnAction(e -> {
            try {
                showTeamSelectScreen(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Error: " + ex.getMessage());
            }
        });
        root.getChildren().addAll(title,description,playButton);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #bb69e1; -fx-background-color: #1a1a2e;");
        root.prefWidthProperty().bind(scrollPane.widthProperty());

       stage.setScene(new Scene(scrollPane));
    }

    public void  showTeamSelectScreen(Stage stage){
        stage.setTitle("Door Dash: Scare vs Laugh Touchdown");
  
        Label title = new Label("CHOOSE YOUR SIDE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setStyle("-fx-text-fill: white;");
     
        Label subtitle = new Label("Who will prove their worth on the Floor?");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setStyle("-fx-text-fill: #cccccc;");
        // ---------- SCARER Card (Sully - blue guy) ----------
        VBox scarerCard = buildRoleCard(
            "SCARER",
            "/game/images/blue_guy.jpeg",
            "Harness the power of screams!\nTerrorize children for energy.\nBring fear to the Floor.",
            "#ff4444"  // red accent
        );
     
        // ---------- LAUGHER Card (Mike - green guy) ----------
        VBox laugherCard = buildRoleCard(
            "LAUGHER",
            "/game/images/green_guy.jpeg",
            "Revolutionize with laughter!\nLaughter produces 10x more energy.\nBring joy to the Floor.",
            "#44ff44"  // green accent
        );
     
        // ---------- Click Actions ----------
        scarerCard.setOnMouseClicked(e -> {
            try {
               //this.game = new Game(Role.SCARER);
               // this.controller = new GameController(this.game, this);
                //showMonsterRevealScreen(stage);#
            	controller.selectRole(Role.SCARER, stage);
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to load game data: " + ex.getMessage());
            }
        });
     
        laugherCard.setOnMouseClicked(e -> {
            try {
                //this.game = new Game(Role.LAUGHER);
                //this.controller = new GameController(this.game, this);
                //showMonsterRevealScreen(stage);
            	controller.selectRole(Role.LAUGHER, stage);
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Failed to load game data: " + ex.getMessage());
            }
        });
     
        // ---------- Cards Row ----------
        HBox cardsRow = new HBox(60, scarerCard, laugherCard);
        cardsRow.setAlignment(Pos.CENTER);
     
        // ---------- Back Button ----------
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
     
        // ---------- Root Layout ----------
        VBox root = new VBox(30, title, subtitle, cardsRow, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #1a1a2e;");
     
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.centerOnScreen();
        

    }
    //this method  is to create the id cardsof the  monsters
    private VBox buildRoleCard(String roleName, String imagePath,
            String description, String accentColor) {

			// --- ID Card Image ---
			ImageView idCard = new ImageView(
			new Image(getClass().getResourceAsStream(imagePath))
			);
			idCard.setFitWidth(220);
			idCard.setFitHeight(160);
			idCard.setPreserveRatio(true);
			
			// --- Role Name ---
			Label roleLabel = new Label(roleName);
			roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
			roleLabel.setStyle("-fx-text-fill: " + accentColor + ";");
			
			// --- Description ---
			Label descLabel = new Label(description);
			descLabel.setFont(Font.font("Arial", 13));
			descLabel.setStyle("-fx-text-fill: #dddddd;");
			descLabel.setWrapText(true);
			descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
			descLabel.setMaxWidth(220);
			
			// --- Click Hint ---
			Label clickHint = new Label("Click to choose");
			clickHint.setFont(Font.font("Arial", FontWeight.BOLD, 12));
			clickHint.setStyle("-fx-text-fill: " + accentColor + ";");
			
			// --- Card Container ---
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
			
			// --- Hover Effect ---
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
    	//i commented them because in showTeamSelect it calls the stage of game -nour 
        /*try {
            this.game = new Game(Role.LAUGHER); // need to be changed when player chooses team
            controller = new GameController(this.game, this);
            } 
        catch (IOException e) {
            System.out.println("Failed to load game data");
            return;
        }*/
       // Player panel dynamic nodes
        Label[] playerPanelRefs = new Label[6]; // [0]=energy, [1]=position, [2]=role, [3]=shield, [4]=confusion, [5]=momentum/focus/freeze
        ProgressBar playerEnergyBar = new ProgressBar(0);
        
        // Opponent panel dynamic nodes  
        Label[] opponentPanelRefs = new Label[6];
        ProgressBar opponentEnergyBar = new ProgressBar(0);

        //GridPane board = new GridPane();
        board = new GridPane(); 
        board.setAlignment(Pos.CENTER);
        board.setGridLinesVisible(false);

                // ============================================================
        //  TOP PANEL — drop this into Main.java inside the Game() method
        //  Replace:   HBox top = new HBox();
        //             top.setPrefHeight(120);
        //             top.setStyle("-fx-background-color: #2a2a4e;");
        //  With everything below up to the closing comment
        // ============================================================

        // ---------- Dice ImageView (starts blank, updates after roll) ----------
        ImageView diceImageView = new ImageView();
        diceImageView.setFitWidth(64);
        diceImageView.setFitHeight(64);
        diceImageView.setPreserveRatio(true);
        // store on controller so it can update it after rolling
        controller.setDiceImageView(diceImageView);

        // ---------- Current Turn Label ----------
        Label turnLabel = new Label("Current Turn:");
        turnLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        turnLabel.setStyle("-fx-text-fill: #aaaaaa;");

        Label currentMonsterLabel = new Label(controller.getCurrentMonster().getName());
        currentMonsterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        currentMonsterLabel.setStyle("-fx-text-fill: white;");
        // store on controller so it can update after each turn
        controller.setCurrentMonsterLabel(currentMonsterLabel);

        VBox turnInfo = new VBox(2, turnLabel, currentMonsterLabel);
        turnInfo.setAlignment(Pos.CENTER_LEFT);

        // ---------- Roll Dice Button ----------
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
        rollButton.setOnAction(e -> {
    try {
        controller.playTurn();
        int roll = controller.getLastRoll();

        // disable button during animation so player can't spam it
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

        currentMonsterLabel.setText(
            controller.getCurrentMonster().getName()
        );
        refreshMonsterPanels(
        controller.getPlayer(), controller.getOpponent(),
        playerPanelRefs, playerEnergyBar,
        opponentPanelRefs, opponentEnergyBar
            );
        controller.refreshBoard(board, cellSize);
        // animate tokens to new positions
        animateTokenToCell(
            playerToken,
            controller.getPlayerPosition() - 1,
            null
        );
        animateTokenToCell(
            opponentToken,
            controller.getOpponentPosition() - 1,
            null
        );

        Monster winner = controller.getWinner();
        if (winner != null) {
            showWinScreen(stage, winner);
        }
        Card drawnCard = controller.getLastCardDrawn();

        if (drawnCard != null) {
            showCardDrawnPopup(drawnCard);
            controller.clearLastCardDrawn();
        }

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


       // ---------- Powerup Box ----------
Label powerupTitle = new Label("POWER UP");
powerupTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
powerupTitle.setStyle("-fx-text-fill: #1a1a2e;");  // dark text on gold bg

Button powerupButton = new Button("-500 ⚡");
powerupButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
powerupButton.setStyle(
    "-fx-background-color: #1a1a2e;" +   // dark button inside gold box
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
    "-fx-background-color: #ffcc00;" +   // fully filled gold
    "-fx-border-color: #ffd700;" +
    "-fx-border-width: 2;" +
    "-fx-border-radius: 10;" +
    "-fx-background-radius: 10;"
);

        // ---------- Assemble Top Panel ----------
        HBox top = new HBox(30);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 30, 0, 30));
        top.setPrefHeight(100);
        top.setStyle("-fx-background-color: #3d3d6b; -fx-border-color: #4a4a80; -fx-border-width: 0 0 2 0;");

        // left: turn info
        HBox leftSection = new HBox(16, turnInfo);
        leftSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftSection, Priority.ALWAYS);

        // center: roll + dice image
        HBox centerSection = new HBox(16, rollButton, diceImageView);
        centerSection.setAlignment(Pos.CENTER);
        HBox.setHgrow(centerSection, Priority.ALWAYS);

        // right: powerup
        HBox rightSection = new HBox(powerupBox);
        rightSection.setAlignment(Pos.CENTER_RIGHT);
        rightSection.setPadding(new Insets(0, 20, 0, 0));  // 20px breathing room from edge
        HBox.setHgrow(rightSection, Priority.ALWAYS);

        top.getChildren().addAll(leftSection, centerSection, rightSection);

        // ============================================================
        //  END OF TOP PANEL
        // ============================================================
        HBox bottom = new HBox();
        bottom.setPrefHeight(120);
        bottom.setStyle("-fx-background-color: #2a2a4e;");

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
    
        //Pane overlay = new Pane();
        
        //overlay.setMouseTransparent(true); // so it doesn't block clicks
        overlay = new Pane();           // ← ADD THIS
        overlay.setMouseTransparent(true);
        StackPane boardWithOverlay = new StackPane();
        boardWithOverlay.getChildren().addAll(board, overlay);
        root.setCenter(boardWithOverlay);
        controller.loadBoard(board,cellSize);
        // create player token
playerToken = new ImageView(
    new Image(getClass().getResourceAsStream(
        getMonsterImagePath(controller.getPlayer().getName())
    ))
);
playerToken.setFitWidth(40);
playerToken.setFitHeight(40);
playerToken.setPreserveRatio(true);
playerToken.setMouseTransparent(true);

// create opponent token
opponentToken = new ImageView(
    new Image(getClass().getResourceAsStream(
        getMonsterImagePath(controller.getOpponent().getName())
    ))
);
opponentToken.setFitWidth(40);
opponentToken.setFitHeight(40);
opponentToken.setPreserveRatio(true);
opponentToken.setMouseTransparent(true);

// add both to overlay
overlay.getChildren().addAll(playerToken, opponentToken);

// place them at starting position
placeTokenAtCell(playerToken, 0);
placeTokenAtCell(opponentToken, 0);
        
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
    overlay.getChildren().clear();
    controller.drawTransports(overlay, board);
    // re-add tokens after clearing
    overlay.getChildren().addAll(playerToken, opponentToken);
    placeTokenAtCell(playerToken, controller.getPlayerPosition() - 1);
    placeTokenAtCell(opponentToken, controller.getOpponentPosition() - 1);
});

stage.heightProperty().addListener((obs, oldVal, newVal) -> {
    overlay.getChildren().clear();
    controller.drawTransports(overlay, board);
    // re-add tokens after clearing
    overlay.getChildren().addAll(playerToken, opponentToken);
    placeTokenAtCell(playerToken, controller.getPlayerPosition() - 1);
    placeTokenAtCell(opponentToken, controller.getOpponentPosition() - 1);
});
        controller.drawTransports(overlay, board);
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.centerOnScreen();
    }
    private String getTypeOfMonster(Monster monster){
    	String result = (monster instanceof Dasher)?"Dasher":(monster instanceof Dynamo)?"Dynamo":(monster instanceof MultiTasker)?"MultiTasker":(monster instanceof Schemer)?"Schemer":"Unknown";
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
        errorStage.show(); }

    private VBox buildMonsterCard(Monster monster, String label,
            String bgColor, String accentColor) {
			// --- YOU / OPPONENT label ---
			Label playerLabel = new Label(label);
			playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			playerLabel.setStyle(
			"-fx-text-fill: white;" +
			"-fx-background-color: " + accentColor + ";" +
			"-fx-padding: 4 16 4 16;" +
			"-fx-background-radius: 12;"
			);
			
			// getting the image of the monster
			String imagePath = getMonsterImagePath(monster.getName());
			ImageView monsterImage = new ImageView(
			new Image(getClass().getResourceAsStream(imagePath))
			);
			monsterImage.setFitWidth(70);
            monsterImage.setFitHeight(60);
			monsterImage.setPreserveRatio(true);
			
			// getting the name of the monster 
			Label nameLabel = new Label(monster.getName());
			nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
			nameLabel.setStyle("-fx-text-fill: " + accentColor + ";");
			
			// getting the type of the monster 
			Label typeLabel = new Label("Type: " + getTypeOfMonster(monster));
			typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			typeLabel.setStyle("-fx-text-fill: #eeeeee;");
			
			// getting the role of the monster 
			Label roleLabel = new Label("Role: " + monster.getOriginalRole().toString());
			roleLabel.setFont(Font.font("Arial", 13));
			roleLabel.setStyle("-fx-text-fill: #cccccc;");
			
			// getting the energy of the monster 
			Label energyLabel = new Label("⚡ Starting Energy: " + monster.getEnergy());
			energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
			energyLabel.setStyle("-fx-text-fill: #ffcc00;");
			
			Separator sep = new Separator();
			
			// the description 
			Label descTitle = new Label("Special Ability:");
			descTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
			descTitle.setStyle("-fx-text-fill: " + accentColor + ";");
			
			Label descLabel = new Label(monster.getDescription());
			descLabel.setFont(Font.font("Arial", 12));
			descLabel.setStyle("-fx-text-fill: #dddddd;");
			descLabel.setWrapText(true);
			descLabel.setMaxWidth(230);
			descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
			
			// --- Assemble Card ---
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
			return card;
}
//im testing
private String getMonsterImagePath(String name) {
    switch (name) {
        case "James P. Sullivan":   return "/game/images/james_sullivanID.png";
        case "Mike Wazowski":       return "/game/images/mike_wazowskiID.png";
        case "Randall Boggs":       return "/game/images/Randall_BoggsID.jpg";
        case "Celia Mae":           return "/game/images/celia_maeID.jpg";  // ← jpg not png!
        case "Fungus":              return "/game/images/fungus2.png";
        case "Yeti":                return "/game/images/yetiID.png";
        default:                    return "/game/images/green_guy.jpeg";
    }
}
    public void showMonsterRevealScreen(Stage stage) {
        stage.setTitle("Door Dash – Your Monster");
     
        Monster player = controller.getPlayer();
        Monster opponent = controller.getOpponent();
     
        // ---------- Title ----------
        Label title = new Label("THE COMPETITORS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setStyle("-fx-text-fill: white;");
     
        Label subtitle = new Label("The Floor awaits. Only one monster will emerge victorious.");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: #aaaaaa;");
     
        // ---------- Monster Cards ----------
        VBox playerCard   = buildMonsterCard(player,   "YOU",      "#1a3a5c", "#4fc3f7");
        VBox opponentCard = buildMonsterCard(opponent,  "OPPONENT", "#3a1a1a", "#ff6b6b");
     
        Label vsLabel = new Label("VS");
        vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        vsLabel.setStyle("-fx-text-fill: #ffcc00;");
     
        HBox cardsRow = new HBox(40, playerCard, vsLabel, opponentCard);
        cardsRow.setAlignment(Pos.CENTER);
     
        // ---------- Start Game Button ----------
        Button startButton = new Button("▶  START GAME");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        startButton.setStyle(
            "-fx-background-color: #ff6600;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 14 40 14 40;" +
            "-fx-background-radius: 30;"
        );
        startButton.setOnAction(e -> Game(stage));
     
        // ---------- Back Button ----------
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
     
        // ---------- Root Layout ----------
        VBox root = new VBox(28, title, subtitle, cardsRow, startButton, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a2e;");
     
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.centerOnScreen();
    }
    // ============================================================
//  Add this helper to Main.java alongside showErrorAlert()
// ============================================================

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

// ============================================================
//  Also add this stub to Main.java — you'll flesh it out later
// ============================================================
public void showWinScreen(Stage stage, Monster winner) {
    playMusic("/game/audio/welcome.mp3");

    // winner image
    ImageView winnerImage = new ImageView(
        new Image(getClass().getResourceAsStream(
            getMonsterImagePath(winner.getName())
        ))
    );
    winnerImage.setFitWidth(200);
    winnerImage.setFitHeight(180);
    winnerImage.setPreserveRatio(true);

    Label crownLabel = new Label("👑");
    crownLabel.setFont(Font.font("Arial", 60));

    Label winnerTitle = new Label("WINNER!");
    winnerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 52));
    winnerTitle.setStyle("-fx-text-fill: #ffcc00;");

    Label winnerName = new Label(winner.getName());
    winnerName.setFont(Font.font("Arial", FontWeight.BOLD, 32));
    winnerName.setStyle("-fx-text-fill: white;");

    Label winnerEnergy = new Label("⚡ Final Energy: " + winner.getEnergy());
    winnerEnergy.setFont(Font.font("Arial", 18));
    winnerEnergy.setStyle("-fx-text-fill: #ffcc00;");

    Button playAgainBtn = new Button("🔄  PLAY AGAIN");
    playAgainBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    playAgainBtn.setStyle(
        "-fx-background-color: #ff6600;" +
        "-fx-text-fill: white;" +
        "-fx-padding: 12 36 12 36;" +
        "-fx-background-radius: 28;" +
        "-fx-cursor: hand;"
    );
    playAgainBtn.setOnAction(e -> {
        this.controller = new GameController(this);
        WelcomeStage(stage);
    });

    VBox root = new VBox(20,
        crownLabel, winnerImage, winnerTitle, winnerName, winnerEnergy, playAgainBtn
    );
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(60));
    root.setStyle("-fx-background-color: #1a1a2e;");

    stage.setScene(new Scene(root));
    stage.setMaximized(true);
    stage.centerOnScreen();
    }
public void showCardDrawnPopup(Card card) {
    Stage popup = new Stage();
    //popup.initModality(Modality.APPLICATION_MODAL); // blocks game until dismissed
    popup.initModality(Modality.NONE);
    // --- Icon ---
    String iconPath = getCardIconPath(card);
    ImageView icon = new ImageView(
        new Image(getClass().getResourceAsStream(iconPath))
    );
    icon.setFitWidth(80);
    icon.setFitHeight(80);
    icon.setPreserveRatio(true);

    // --- Card Drawn header ---
    Label header = new Label("🃏  CARD DRAWN");
    header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    header.setStyle("-fx-text-fill: #aaaaaa;");

    // --- Card Name ---
    Label nameLabel = new Label(card.getName());
    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
    nameLabel.setStyle("-fx-text-fill: #ffcc00;");
    nameLabel.setWrapText(true);
    nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

    // --- Effect ---
    Label effectLabel = new Label(card.getDescription());
    effectLabel.setFont(Font.font("Arial", 15));
    effectLabel.setStyle("-fx-text-fill: #dddddd;");
    effectLabel.setWrapText(true);
    effectLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    effectLabel.setMaxWidth(260);

    // --- Lucky/Unlucky badge ---
    Label luckyLabel = new Label(card.isLucky() ? "✨ Lucky!" : "💀 Unlucky!");
    luckyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
    luckyLabel.setStyle(card.isLucky()
        ? "-fx-text-fill: #44ff88; -fx-background-color: #1a3a2a; -fx-padding: 4 12 4 12; -fx-background-radius: 10;"
        : "-fx-text-fill: #ff4444; -fx-background-color: #3a1a1a; -fx-padding: 4 12 4 12; -fx-background-radius: 10;"
    );

    // --- Continue Button ---
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

    // --- Assemble ---
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

    // slide-up animation
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
    if (card instanceof SwapperCard)    return "/game/images/position_swap.png";
    if (card instanceof ShieldCard)     return "/game/images/sheild_icon.png";
    if (card instanceof EnergyStealCard) return "/game/images/small_thief.png";
    if (card instanceof StartOverCard)  return "/game/images/square_one.png";
    if (card instanceof ConfusionCard)  return "/game/images/confused_icon.png";
    return null;
}
//    Builds one monster info panel (used for both player & opponent)
// ============================================================
 
private VBox buildMonsterInfoPanel(Monster monster, String label,
        String accentColor, Label[] refs, ProgressBar energyBar, Stage stage) {
 
    // --- YOU / OPPONENT badge ---
    Label badge = new Label(label);
    badge.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    badge.setStyle(
        "-fx-text-fill: white;" +
        "-fx-background-color: " + accentColor + ";" +
        "-fx-padding: 3 12 3 12;" +
        "-fx-background-radius: 10;"
    );
 
    // --- Monster Image ---
    String imagePath = getMonsterImagePath(monster.getName());
    ImageView monsterImage = new ImageView(
        new Image(getClass().getResourceAsStream(imagePath))
    );
    monsterImage.setFitWidth(100);
    monsterImage.setFitHeight(80);
    monsterImage.setPreserveRatio(true);
 
    // --- Name ---
    Label nameLabel = new Label(monster.getName());
    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    nameLabel.setStyle("-fx-text-fill: " + accentColor + ";");
 
    // --- Type badge ---
    Label typeLabel = new Label(getTypeOfMonster(monster));
    typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
    typeLabel.setStyle(
        "-fx-text-fill: #1a1a2e;" +
        "-fx-background-color: " + accentColor + ";" +
        "-fx-padding: 2 10 2 10;" +
        "-fx-background-radius: 8;"
    );
 
    // --- Original Role (static) ---
    Label originalRoleLabel = new Label("Role: " + monster.getOriginalRole());
    originalRoleLabel.setFont(Font.font("Arial", 11));
    originalRoleLabel.setStyle("-fx-text-fill: #aaaaaa;");
 
    // --- Current Role (changes when confused) ---
    Label currentRoleLabel = new Label("Current: " + monster.getRole());
    currentRoleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
    currentRoleLabel.setStyle("-fx-text-fill: white;");
    refs[2] = currentRoleLabel;
 
    // --- Position ---
    Label positionLabel = new Label("📍 Position: " + monster.getPosition());
    positionLabel.setFont(Font.font("Arial", 11));
    positionLabel.setStyle("-fx-text-fill: #cccccc;");
    refs[1] = positionLabel;
 
    // --- Energy Label ---
    Label energyLabel = new Label("⚡ " + monster.getEnergy() + " / 1000");
    energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    energyLabel.setStyle("-fx-text-fill: #ffcc00;");
    refs[0] = energyLabel;
 
    // --- Energy Bar ---
    double energyPercent = Math.min((double) monster.getEnergy() / 1000.0, 1.0);
    energyBar.setProgress(energyPercent);
    energyBar.prefWidthProperty().bind(stage.widthProperty().divide(8));
    energyBar.setStyle(getEnergyBarStyle(energyPercent));
 
    VBox energySection = new VBox(4, energyLabel, energyBar);
    energySection.setAlignment(Pos.CENTER);
 
    // --- Status Effects ---
    Label shieldLabel = new Label();
    shieldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
    refs[3] = shieldLabel;
 
    Label confusionLabel = new Label();
    confusionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
    refs[4] = confusionLabel;
 
    Label specialLabel = new Label(); // momentum / focus / frozen
    specialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
    refs[5] = specialLabel;
 
    VBox statusBox = new VBox(3, shieldLabel, confusionLabel, specialLabel);
    statusBox.setAlignment(Pos.CENTER);
 
    // update status effects initially
    refreshStatusLabels(monster, refs);
 
    Separator sep = new Separator();
 
    // --- Assemble Card ---
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
 
    return card;
}
// 3. ADD THIS HELPER — returns correct energy bar color style
// ============================================================
 
private String getEnergyBarStyle(double percent) {
    String color;
    if (percent > 0.6)      color = "#44ff88"; // green
    else if (percent > 0.3) color = "#ffcc00"; // yellow
    else                    color = "#ff4444"; // red
 
    return "-fx-accent: " + color + ";" +
           "-fx-background-color: #1a1a2e;" +
           "-fx-background-radius: 6;" +
           "-fx-pref-height: 12;";
}
// 4. ADD THIS HELPER — refreshes all status effect labels
// ============================================================
 
private void refreshStatusLabels(Monster monster, Label[] refs) {
    // shield
    if (monster.isShielded()) {
        refs[3].setText("🛡 Shielded");
        refs[3].setStyle("-fx-text-fill: #4fc3f7; -fx-background-color: #1a3a5c; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
    } else {
        refs[3].setText("");
        refs[3].setStyle("");
    }
 
    // confusion
    if (monster.isConfused()) {
        refs[4].setText("😵 Confused (" + monster.getConfusionTurns() + ")");
        refs[4].setStyle("-fx-text-fill: #ff44ff; -fx-background-color: #2a1a3a; -fx-padding: 2 8 2 8; -fx-background-radius: 8;");
    } else {
        refs[4].setText("");
        refs[4].setStyle("");
    }
 
    // frozen / momentum / focus
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
// 5. ADD THIS METHOD — call it after every roll to refresh both panels
// ============================================================
 
private void refreshMonsterPanels(Monster player, Monster opponent,
        Label[] playerRefs, ProgressBar playerBar,
        Label[] opponentRefs, ProgressBar opponentBar) {
 
    // --- Player ---
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
 
    // --- Opponent ---
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
}
private void playMusic(String audioPath) {
    if (currentMusic != null) {
        currentMusic.stop();
    }
    java.net.URL resource = getClass().getResource(audioPath);
    if (resource == null) return;
    javafx.scene.media.Media media = new javafx.scene.media.Media(resource.toString());
    currentMusic = new javafx.scene.media.MediaPlayer(media);
    currentMusic.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE); // loops forever
    currentMusic.play();
    }

    private void placeTokenAtCell(ImageView token, int cellIndex) {
    javafx.scene.Node cell = controller.getNodeFromGrid(board, cellIndex);
    if (cell == null) return;

    Platform.runLater(() -> {
        Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
        Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());

        double centerX = (cellBounds.getMinX() + cellBounds.getMaxX()) / 2 
                         - overlayBounds.getMinX();
        double centerY = (cellBounds.getMinY() + cellBounds.getMaxY()) / 2 
                         - overlayBounds.getMinY();

        token.setX(centerX - token.getFitWidth() / 2);
        token.setY(centerY - token.getFitHeight() / 2);
    });
}

private void animateTokenToCell(ImageView token, int targetIndex, Runnable onFinished) {
    // figure out current cell index from token position
    int currentIndex = getTokenCurrentIndex(token);
    
    if (currentIndex == targetIndex) {
        if (onFinished != null) onFinished.run();
        return;
    }
    
    // build list of cells to pass through
    // step one cell at a time toward target
    int step = (targetIndex > currentIndex) ? 1 : -1;
    ArrayList<Integer> steps = new ArrayList<>();
    for (int i = currentIndex + step; i != targetIndex + step; i += step) {
        steps.add(i);
    }
    
    // animate step by step
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
        Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
        Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());
        
        double targetX = (cellBounds.getMinX() + cellBounds.getMaxX()) / 2
                         - overlayBounds.getMinX() - token.getFitWidth() / 2;
        double targetY = (cellBounds.getMinY() + cellBounds.getMaxY()) / 2
                         - overlayBounds.getMinY() - token.getFitHeight() / 2;
        
        // each step takes 120ms
        Timeline move = new Timeline(
            new KeyFrame(Duration.millis(120),
                new KeyValue(token.xProperty(), targetX,
                    javafx.animation.Interpolator.EASE_BOTH),
                new KeyValue(token.yProperty(), targetY,
                    javafx.animation.Interpolator.EASE_BOTH)
            )
        );
        
        move.setOnFinished(e ->
            animateSteps(token, steps, stepIndex + 1, onFinished) // next step
        );
        
        move.play();
    });
}

// helper — finds which cell index the token is currently sitting at
private int getTokenCurrentIndex(ImageView token) {
    for (int i = 0; i < Constants.BOARD_ROWS * Constants.BOARD_COLS; i++) {
        javafx.scene.Node cell = controller.getNodeFromGrid(board, i);
        if (cell == null) continue;
        
        Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
        Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());
        
        double centerX = (cellBounds.getMinX() + cellBounds.getMaxX()) / 2
                         - overlayBounds.getMinX() - token.getFitWidth() / 2;
        double centerY = (cellBounds.getMinY() + cellBounds.getMaxY()) / 2
                         - overlayBounds.getMinY() - token.getFitHeight() / 2;
        
        // if token is close to this cell's center — it's here
        if (Math.abs(token.getX() - centerX) < 5 && Math.abs(token.getY() - centerY) < 5) {
            return i;
        }
    }
    return 0; // default to cell 0
}
    

    public static void main(String[] args) {
        launch(args);
    }
}