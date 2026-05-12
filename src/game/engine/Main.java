package game.engine;
import game.engine.monsters.*;
import javafx.scene.image.*;
import java.io.IOException;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.beans.binding.*;

public class Main extends Application {
    private GameController controller;
    private Game game;
    public void start(Stage stage){
    	//i updated the constructor of game controller as well to create the game controller as soon as the view is launched 
    	this.controller = new GameController(this);
        stage.setMinHeight(700);
        stage.setMinWidth(1000);
        stage.setMaximized(true);
        WelcomeStage(stage);
        stage.show();
    }
    public void WelcomeStage(Stage stage) {
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
    	//i commented them because in showTeamSelect it calls the stage of game -nour 
        /*try {
            this.game = new Game(Role.LAUGHER); // need to be changed when player chooses team
            controller = new GameController(this.game, this);
            } 
        catch (IOException e) {
            System.out.println("Failed to load game data");
            return;
        }*/

        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setGridLinesVisible(false);

        HBox top = new HBox();
        top.setPrefHeight(120);
        top.setStyle("-fx-background-color: #2a2a4e;");

        HBox bottom = new HBox();
        bottom.setPrefHeight(120);
        bottom.setStyle("-fx-background-color: #2a2a4e;");

        NumberBinding cellSize = Bindings.min(
            stage.widthProperty().subtract(240).divide(10),
            stage.heightProperty().subtract(240).divide(10)
        );
        VBox left = new VBox(10);
        left.prefWidthProperty().bind(
            stage.widthProperty().subtract(cellSize.multiply(10)).divide(2)
            );
        left.setAlignment(Pos.CENTER);
        left.setStyle("-fx-background-color: #2a2a4e;");

        VBox right = new VBox(10);
        right.prefWidthProperty().bind(
        stage.widthProperty().subtract(cellSize.multiply(10)).divide(2)
        );
        right.setAlignment(Pos.CENTER);
        right.setStyle("-fx-background-color: #2a2a4e;");

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
    
        Pane overlay = new Pane();
        overlay.setMouseTransparent(true); // so it doesn't block clicks
        StackPane boardWithOverlay = new StackPane();
        boardWithOverlay.getChildren().addAll(board, overlay);
        root.setCenter(boardWithOverlay);
        controller.loadBoard(board,cellSize);
        
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                overlay.getChildren().clear();
                controller.drawTransports(overlay, board);
                });
                stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                    overlay.getChildren().clear();
                    controller.drawTransports(overlay, board);
                });

        controller.drawTransports(overlay, board);
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
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
			monsterImage.setFitWidth(180);
			monsterImage.setFitHeight(130);
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
    private String getMonsterImagePath(String name) {
    switch (name) {
        case "James P. Sullivan":   return "/game/images/james_sullivanID.png";
        case "Mike Wazowski":       return "/game/images/mike_wazowskiID.png";
        case "Randall Boggs":       return "/game/images/Randall_BoggsID.jpg";
        case "Celia Mae":           return "/game/images/celia_maeID.png";
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
    }

    
    


    


    public static void main(String[] args) {
        launch(args);
    }
}