package game.engine;

import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
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

    public GameController (Game game ,Main mainView){
        this.game = game;
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
    public String getImagePath(Cell cell,int i){  
        if(cell instanceof DoorCell){
            if(i == Constants.BOARD_COLS*Constants.BOARD_ROWS-1){
                return "/game/images/winnerdoor.png";
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
    
}
