package game.engine;

import java.io.IOException;
import java.util.Random;

import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.beans.binding.*;



public class GameController {
    private Game game;
    private Main mainView;
    private int doorcount=0;

    /*public GameController (Game game ,Main mainView){
        this.game = game;
        this.mainView = mainView;
    }*/
    public GameController(Main mainView) {
        this.mainView = mainView;
    }

    public void loadBoard(GridPane board,NumberBinding cellsize){
        Cell[ ][] cells = game.getBoard().getBoardCells();

        for(int i =0 ; i<Constants.BOARD_ROWS*Constants.BOARD_COLS;i++){
                int[] rowCol = indexToRowCol(i);
                Cell cell = cells[rowCol[0]][rowCol[1]];
                if(cell instanceof DoorCell && i != Constants.BOARD_COLS * Constants.BOARD_ROWS - 1){
                    doorcount++;
                }
                String imagePath = getImagePath(cell,i);
                StackPane stackPane =(StackPane) getNodeFromGrid(board,i);
                if(stackPane !=null && imagePath !=null){
                    ImageView iv = new ImageView(
                        new Image(getClass().getResourceAsStream(imagePath))
                    ); 
                    iv.fitWidthProperty().bind(cellsize);
                    iv.fitHeightProperty().bind(cellsize);
                    iv.setPreserveRatio(false);
                    stackPane.getChildren().add(iv);
                }
                    Label indexLabel = new Label ( Integer.toString(i+1));
                indexLabel.setStyle(
                    "-fx-font-family: 'Comic Sans MS';" +       // bold blocky font
                    "-fx-font-size: 13;" +
                    "-fx-text-fill: #f9f9f9;" +          // sulley purple
                    "-fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 0);"
                );
                    StackPane.setAlignment(indexLabel, Pos.TOP_LEFT);
                    stackPane.getChildren().add(indexLabel);
                    
            }

    }
    // drawing the ladders for contamination socks and hanging doors for conveyer belts

    public void drawTransports (Pane overlay,GridPane board){
        Cell [][] cells = game.getBoard().getBoardCells();
        int max_size = Constants.BOARD_COLS*Constants.BOARD_ROWS;
        for(int i =0 ; i<max_size;i++){
            int[] rowCol = indexToRowCol(i);
            int x =rowCol[0];
            int y = rowCol[1];
            Cell cell = cells[x][y];
            if(cell instanceof ContaminationSock){
                int effect = ((ContaminationSock)cell).getEffect();
                if(i+effect<max_size)
                    drawLadder(overlay, board, i, i+effect);
            }
            if(cell instanceof ConveyorBelt){
                int effect = ((ConveyorBelt)cell).getEffect();
                if(i+effect<max_size)
                    drawDoorLine(overlay, board, i, i+effect);
            }
        }

    }
    public String getImagePath(Cell cell,int i){  
        if(cell instanceof DoorCell){
            if(i == Constants.BOARD_COLS*Constants.BOARD_ROWS-1){
                return "/game/images/winnerdoor2.jpeg";
            }
            int doorNumber = (doorcount % 2) + 1;
            if (((DoorCell) cell).isActivated()) {
                return "/game/images/door" + doorNumber + "_open.png";
            } else {
                return "/game/images/door" + doorNumber + "_closed.png";
            }
            
        }
        else if (cell instanceof CardCell ){
            return "/game/images/card.png";
        }
        else if (cell instanceof ContaminationSock){
            return "/game/images/sock.png";
        }
        else if (cell instanceof ConveyorBelt){
            return "/game/images/conveyor.png";
        }
        else if (cell instanceof MonsterCell){
            String monsterName = ((MonsterCell)cell).getName();
            switch (monsterName) {
            case "Mike Wazowski": return "/game/images/Mike_Wazowski.png";
            case "James P. Sullivan":   return "/game/images/James_P.Sullivan.png";
            case "Randall Boggs":    return "/game/images/Randall_Boggs.png";
            case "Celia Mae":  return "/game/images/Celia_Mae.png";
            case "Fungus":  return "/game/images/Fungus.png";
            case "Yeti":  return "/game/images/Yeti.png";
            default:       return null; 
            }
            
        }
        return null;
    }
    // Gridepane doesnt have getcell so we need to look it up
    public javafx.scene.Node getNodeFromGrid(GridPane grid, int index){
        int[] rowCol = indexToRowCol(index);
        int col = rowCol[1];
        int row = Constants.BOARD_ROWS - 1 - rowCol[0];
        for(javafx.scene.Node node :grid.getChildren()){
            if (GridPane.getColumnIndex(node)== col && GridPane.getRowIndex(node)==row){
                return node;
            }
        }
        return null;
    }

    private int[] indexToRowCol(int index) {
    int row = index / Constants.BOARD_COLS;
    int col = index % Constants.BOARD_COLS;
    if (row % 2 == 1) {
        col = Constants.BOARD_COLS - 1 - col;
    }
    return new int[]{row, col};
    }

public void drawLadder(Pane overlay, GridPane board, int fromIndex, int toIndex) {
    javafx.scene.Node fromNode = getNodeFromGrid(board, fromIndex);
    javafx.scene.Node toNode = getNodeFromGrid(board, toIndex);

    if (fromNode == null || toNode == null) return;

    Platform.runLater(() -> {
        Bounds fromBounds = fromNode.localToScene(fromNode.getBoundsInLocal());
        Bounds toBounds = toNode.localToScene(toNode.getBoundsInLocal());
        Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());

       /*double fromX = fromBounds.getCenterX() - overlayBounds.getMinX();
        double fromY = fromBounds.getCenterY() - overlayBounds.getMinY();
        double toX = toBounds.getCenterX() - overlayBounds.getMinX();
        double toY = toBounds.getCenterY() - overlayBounds.getMinY();*/
        double fromX = (fromBounds.getMinX() + fromBounds.getMaxX()) / 2 - overlayBounds.getMinX();
        double fromY = (fromBounds.getMinY() + fromBounds.getMaxY()) / 2 - overlayBounds.getMinY();
        double toX = (toBounds.getMinX() + toBounds.getMaxX()) / 2 - overlayBounds.getMinX();
        double toY = (toBounds.getMinY() + toBounds.getMaxY()) / 2 - overlayBounds.getMinY();

        // angle and distance
        double angle = Math.atan2(toY - fromY, toX - fromX);
        double distance = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2));

        double ladderWidth = 12; // gap between the two rails
        double rungSpacing = 15; // space between rungs

        // offset perpendicular to the ladder direction for the two rails
        double perpX = Math.cos(angle + Math.PI / 2) * ladderWidth;
        double perpY = Math.sin(angle + Math.PI / 2) * ladderWidth;

        // left rail
        javafx.scene.shape.Line leftRail = new javafx.scene.shape.Line(
            fromX - perpX, fromY - perpY,
            toX - perpX, toY - perpY
        );
        leftRail.setStroke(javafx.scene.paint.Color.web("#8B4513")); // brown wood color
        leftRail.setStrokeWidth(3);

        // right rail
        javafx.scene.shape.Line rightRail = new javafx.scene.shape.Line(
            fromX + perpX, fromY + perpY,
            toX + perpX, toY + perpY
        );
        rightRail.setStroke(javafx.scene.paint.Color.web("#8B4513"));
        rightRail.setStrokeWidth(3);

        overlay.getChildren().addAll(leftRail, rightRail);

        // rungs
        int numRungs = (int) (distance / rungSpacing);
        for (int r = 1; r < numRungs; r++) {
            double t = (double) r / numRungs;
            double rungCenterX = fromX + t * (toX - fromX);
            double rungCenterY = fromY + t * (toY - fromY);

            javafx.scene.shape.Line rung = new javafx.scene.shape.Line(
                rungCenterX - perpX, rungCenterY - perpY,
                rungCenterX + perpX, rungCenterY + perpY
            );
            rung.setStroke(javafx.scene.paint.Color.web("#A0522D")); // slightly lighter brown
            rung.setStrokeWidth(2);
            overlay.getChildren().add(rung);
        }
    });
}

public void drawDoorLine(Pane overlay, GridPane board, int fromIndex, int toIndex) {
    javafx.scene.Node fromNode = getNodeFromGrid(board, fromIndex);
    javafx.scene.Node toNode = getNodeFromGrid(board, toIndex);

    if (fromNode == null || toNode == null) return;

    Platform.runLater(() -> {
        Bounds fromBounds = fromNode.localToScene(fromNode.getBoundsInLocal());
        Bounds toBounds = toNode.localToScene(toNode.getBoundsInLocal());
        Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());

        /*double fromX = fromBounds.getCenterX() - overlayBounds.getMinX();
        double fromY = fromBounds.getCenterY() - overlayBounds.getMinY();
        double toX = toBounds.getCenterX() - overlayBounds.getMinX();
        double toY = toBounds.getCenterY() - overlayBounds.getMinY();*/
        double fromX = (fromBounds.getMinX() + fromBounds.getMaxX()) / 2 - overlayBounds.getMinX();
        double fromY = (fromBounds.getMinY() + fromBounds.getMaxY()) / 2 - overlayBounds.getMinY();
        double toX = (toBounds.getMinX() + toBounds.getMaxX()) / 2 - overlayBounds.getMinX();
        double toY = (toBounds.getMinY() + toBounds.getMaxY()) / 2 - overlayBounds.getMinY();

        double distance = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2));

       

        // two thin rails to make it look like a track
        javafx.scene.shape.Line rail2 = new javafx.scene.shape.Line(fromX, fromY + 6, toX, toY + 6);
        rail2.setStroke(javafx.scene.paint.Color.web("#333333"));
        rail2.setStrokeWidth(3);
        rail2.setOpacity(0.9);                                          
        overlay.getChildren().add(rail2);

        double gap = 30;
        int numDoors = (int)(distance / gap);
        double stringLength = 6;
        double doorWidth = 14;
        double doorHeight = 20;

        
        String[] doorImages = {
            "/game/images/doorhanging.jpeg",
            "/game/images/doorhanging2.png",
            "/game/images/doorhanging3.png"
            
        };
        Random random = new Random();                                  
        for (int d = 0; d <= numDoors; d++) {
            double t = (double) d / numDoors;
            double hangX = fromX + t * (toX - fromX);
            double hangY = fromY + t * (toY - fromY);

            // â†“ pick a random door image each iteration
            String randomPath = doorImages[random.nextInt(doorImages.length)];
            Image doorImage = new Image(getClass().getResourceAsStream(randomPath));

            // short string from rail to door
            javafx.scene.shape.Line string = new javafx.scene.shape.Line(
                hangX, hangY + 6,
                hangX, hangY + 6 + stringLength
            );
            string.setStroke(javafx.scene.paint.Color.web("#888888"));
            string.setStrokeWidth(1);

            // door image
            ImageView doorView = new ImageView(doorImage);
            doorView.setFitWidth(doorWidth);
            doorView.setFitHeight(doorHeight);
            doorView.setPreserveRatio(false);
            doorView.setX(hangX - doorWidth / 2);
            doorView.setY(hangY + 6 + stringLength);
            doorView.setOpacity(0.9);

            overlay.getChildren().addAll(string, doorView);
        }
    });
}


	public void selectRole(Role role, Stage stage) throws IOException {
	    this.game = new Game(role);
	    mainView.showMonsterRevealScreen(stage);
	}
	
	public Monster getPlayer() {
	    return game.getPlayer();
	}
	
	public Monster getOpponent() {
	    return game.getOpponent();
	}
}
