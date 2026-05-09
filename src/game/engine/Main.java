package game.engine;
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
    "Be the first monster to reach cell 99 (Boo’s Door) with at least 1000 energy\n\n" +
    "How to Win\n" +
    "You win if you:\n" +
    "Reach cell 99, AND\n" +
    "Have 1000 or more energy\n\n" +
    "On Your Turn:\n\n" +
    "(Optional) Use your monster powerup (costs 500 energy if used manually).\n\n" +
    "Roll a 6-sided dice (1–6).\n" +
    "Move forward that number of cells.\n" +
    "Apply the effect of the cell you land on:\n" +
    "- Doors → gain or lose energy for your whole team\n" +
    "- Cards → draw a random card effect\n" +
    "- Monster cells → trigger special monster interactions\n" +
    "- Conveyor/Socks → move forward/backward with effects\n" +
    "- Normal cell → nothing happens\n\n" +
    "If you land on the opponent’s cell → your move is cancelled and you retry."
        );
        description.setWrapText(true);
        description.setFont(Font.font("Arial", 16));
        description.setStyle("-fx-text-fill: #021b5e;");
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #bb69e1;");

        Button playButton = new Button("▶  PLAY");
        playButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        playButton.setStyle("-fx-background-color: #ff6600; -fx-text-fill: white; -fx-padding: 14 40 14 40; -fx-background-radius: 30;");
        playButton.setOnAction(e -> Game(stage));
        
        root.getChildren().addAll(title,description,playButton);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #bb69e1; -fx-background-color: #1a1a2e;");
        root.prefWidthProperty().bind(scrollPane.widthProperty());

       stage.setScene(new Scene(scrollPane));
    }

    public void  showTeamSelectScreen(Stage stage){

    }

    public void Game(Stage stage) {
        try {
            this.game = new Game(Role.LAUGHER); // need to be changed when player chooses team
            controller = new GameController(this.game, this);
            } 
        catch (IOException e) {
            System.out.println("Failed to load game data");
            return;
        }

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
        root.setCenter(board);
        controller.loadBoard(board,cellSize);
        
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
    }


    


    public static void main(String[] args) {
        launch(args);
    }
}