package game.engine;


import java.io.IOException;
import java.util.Random;


import game.engine.cards.Card;
import game.engine.cards.ConfusionCard;
import game.engine.cards.EnergyStealCard;
import game.engine.cards.ShieldCard;
import game.engine.cards.StartOverCard;
import game.engine.cards.SwapperCard;
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
import javafx.animation.*;
import javafx.util.Duration;




public class GameController {
    private Game game;
    private Main mainView;
    private Label currentMonsterLabel;
    private ImageView diceImageView;


    public GameController(Main mainView) {
        this.mainView = mainView;
    }
    public int getCardsRemaining() {
        return Board.getCards().size();
    }


    public void loadBoard(GridPane board,NumberBinding cellsize){
        Cell[ ][] cells = game.getBoard().getBoardCells();


        for(int i =0 ; i<Constants.BOARD_ROWS*Constants.BOARD_COLS;i++){
                int[] rowCol = indexToRowCol(i);
                Cell cell = cells[rowCol[0]][rowCol[1]];
                String imagePath = getImagePath(cell,i);
                StackPane stackPane =(StackPane) getNodeFromGrid(board,i);
                if(stackPane !=null && imagePath !=null){
                    ImageView iv = new ImageView(
                        new Image(getClass().getResourceAsStream(imagePath))
                    );
                    iv.fitWidthProperty().bind(cellsize);
                    iv.fitHeightProperty().bind(cellsize);
                    iv.setPreserveRatio(false);
                    iv.setId("cell-image-" + i); // ← TAG IT with the cell index
                    stackPane.getChildren().add(iv);
                }
                    Label indexLabel = new Label ( Integer.toString(i+1));
                    if (cell instanceof DoorCell && i != Constants.BOARD_COLS * Constants.BOARD_ROWS - 1) {
    String doorColor = ((DoorCell)cell).getRole() == Role.SCARER ? "#cc2222" : "#2255cc";
    String doorIcon  = ((DoorCell)cell).getRole() == Role.SCARER ? "👹" : "😂";


    // energy top-right (already existed)
    Label energyLabel = new Label("⚡" + ((DoorCell) cell).getEnergy());
    energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
    energyLabel.setStyle(
        "-fx-text-fill: white;" +
        "-fx-background-color: " + doorColor + ";" +
        "-fx-padding: 1 4 1 4;" +
        "-fx-background-radius: 4;"
    );
    StackPane.setAlignment(energyLabel, Pos.TOP_RIGHT);
    stackPane.getChildren().add(energyLabel);


    // role bottom-left (NEW)
    Label roleLabel = new Label(doorIcon + (((DoorCell)cell).getRole() == Role.SCARER ? " S" : " L"));
    roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 8));
    roleLabel.setStyle(
        "-fx-text-fill: white;" +
        "-fx-background-color: " + doorColor + ";" +
        "-fx-padding: 1 3 1 3;" +
        "-fx-background-radius: 4;"
    );
    StackPane.setAlignment(roleLabel, Pos.BOTTOM_LEFT);
    stackPane.getChildren().add(roleLabel);
}
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


        int doorNumber = (i % 2) + 1;


        if (((DoorCell) cell).isActivated()) {
            return "/game/images/door" + doorNumber + "_open.png";
        } else {
            return "/game/images/door" + doorNumber + "_closed.png";
        }
    }
        else if (cell instanceof CardCell ){
            return "/game/images/card_bezn_allah.png";
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
    javafx.scene.Node toNode   = getNodeFromGrid(board, toIndex);
    if (fromNode == null || toNode == null) return;

    Bounds fromBounds    = fromNode.localToScene(fromNode.getBoundsInLocal());
    Bounds toBounds      = toNode.localToScene(toNode.getBoundsInLocal());
    Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());

    double fromX = (fromBounds.getMinX() + fromBounds.getMaxX()) / 2 - overlayBounds.getMinX();
    double fromY = (fromBounds.getMinY() + fromBounds.getMaxY()) / 2 - overlayBounds.getMinY();
    double toX   = (toBounds.getMinX()   + toBounds.getMaxX())   / 2 - overlayBounds.getMinX();
    double toY   = (toBounds.getMinY()   + toBounds.getMaxY())   / 2 - overlayBounds.getMinY();

    double angle       = Math.atan2(toY - fromY, toX - fromX);
    double distance    = Math.hypot(toX - fromX, toY - fromY);
    double ladderWidth = 12;
    double rungSpacing = 15;

    double perpX = Math.cos(angle + Math.PI / 2) * ladderWidth;
    double perpY = Math.sin(angle + Math.PI / 2) * ladderWidth;

    javafx.scene.shape.Line leftRail = new javafx.scene.shape.Line(
        fromX - perpX, fromY - perpY, toX - perpX, toY - perpY
    );
    leftRail.setStroke(javafx.scene.paint.Color.web("#8B4513"));
    leftRail.setStrokeWidth(3);
    leftRail.getStrokeDashArray().setAll(distance);
    leftRail.setStrokeDashOffset(distance);

    javafx.scene.shape.Line rightRail = new javafx.scene.shape.Line(
        fromX + perpX, fromY + perpY, toX + perpX, toY + perpY
    );
    rightRail.setStroke(javafx.scene.paint.Color.web("#8B4513"));
    rightRail.setStrokeWidth(3);
    rightRail.getStrokeDashArray().setAll(distance);
    rightRail.setStrokeDashOffset(distance);

    overlay.getChildren().addAll(leftRail, rightRail);

    java.util.List<javafx.scene.shape.Line> rungs = new java.util.ArrayList<>();
    int numRungs = (int) (distance / rungSpacing);
    for (int r = 1; r < numRungs; r++) {
        double t  = (double) r / numRungs;
        double cx = fromX + t * (toX - fromX);
        double cy = fromY + t * (toY - fromY);
        javafx.scene.shape.Line rung = new javafx.scene.shape.Line(
            cx - perpX, cy - perpY, cx + perpX, cy + perpY
        );
        rung.setStroke(javafx.scene.paint.Color.web("#A0522D"));
        rung.setStrokeWidth(2);
        rung.setOpacity(0);
        overlay.getChildren().add(rung);
        rungs.add(rung);
    }

    // Animate rails drawing in, then cascade-fade the rungs
    Timeline railAnim = new Timeline(new KeyFrame(Duration.millis(350),
        new KeyValue(leftRail.strokeDashOffsetProperty(),  0, Interpolator.EASE_OUT),
        new KeyValue(rightRail.strokeDashOffsetProperty(), 0, Interpolator.EASE_OUT)
    ));
    railAnim.setOnFinished(e -> {
        for (int r = 0; r < rungs.size(); r++) {
            final javafx.scene.shape.Line rung = rungs.get(r);
            PauseTransition delay = new PauseTransition(Duration.millis(r * 18));
            delay.setOnFinished(ev -> {
                FadeTransition ft = new FadeTransition(Duration.millis(100), rung);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            });
            delay.play();
        }
    });
    railAnim.play();
}

public void drawDoorLine(Pane overlay, GridPane board, int fromIndex, int toIndex) {
    javafx.scene.Node fromNode = getNodeFromGrid(board, fromIndex);
    javafx.scene.Node toNode   = getNodeFromGrid(board, toIndex);
    if (fromNode == null || toNode == null) return;

    Bounds fromBounds    = fromNode.localToScene(fromNode.getBoundsInLocal());
    Bounds toBounds      = toNode.localToScene(toNode.getBoundsInLocal());
    Bounds overlayBounds = overlay.localToScene(overlay.getBoundsInLocal());

    double fromX = (fromBounds.getMinX() + fromBounds.getMaxX()) / 2 - overlayBounds.getMinX();
    double fromY = (fromBounds.getMinY() + fromBounds.getMaxY()) / 2 - overlayBounds.getMinY();
    double toX   = (toBounds.getMinX()   + toBounds.getMaxX())   / 2 - overlayBounds.getMinX();
    double toY   = (toBounds.getMinY()   + toBounds.getMaxY())   / 2 - overlayBounds.getMinY();

    double distance = Math.hypot(toX - fromX, toY - fromY);

    javafx.scene.shape.Line rail = new javafx.scene.shape.Line(fromX, fromY + 6, toX, toY + 6);
    rail.setStroke(javafx.scene.paint.Color.web("#333333"));
    rail.setStrokeWidth(3);
    rail.setOpacity(0.9);
    rail.getStrokeDashArray().setAll(distance);
    rail.setStrokeDashOffset(distance);
    overlay.getChildren().add(rail);

    String[] doorImages = {
        "/game/images/doorhanging.jpeg",
        "/game/images/doorhanging2.png",
        "/game/images/doorhanging3.png"
    };
    Random random      = new Random();
    double gap         = 30;
    int    numDoors    = (int) (distance / gap);
    double stringLen   = 6;
    double doorWidth   = 14;
    double doorHeight  = 20;

    java.util.List<javafx.scene.Node> hangItems = new java.util.ArrayList<>();
    for (int d = 0; d <= numDoors; d++) {
        double t     = (double) d / numDoors;
        double hangX = fromX + t * (toX - fromX);
        double hangY = fromY + t * (toY - fromY);

        javafx.scene.shape.Line string = new javafx.scene.shape.Line(
            hangX, hangY + 6, hangX, hangY + 6 + stringLen
        );
        string.setStroke(javafx.scene.paint.Color.web("#888888"));
        string.setStrokeWidth(1);
        string.setOpacity(0);

        ImageView doorView = new ImageView(
            new Image(getClass().getResourceAsStream(doorImages[random.nextInt(doorImages.length)]))
        );
        doorView.setFitWidth(doorWidth);
        doorView.setFitHeight(doorHeight);
        doorView.setPreserveRatio(false);
        doorView.setX(hangX - doorWidth / 2);
        doorView.setY(hangY + 6 + stringLen);
        doorView.setOpacity(0);

        overlay.getChildren().addAll(string, doorView);
        hangItems.add(string);
        hangItems.add(doorView);
    }

    // Animate rail drawing in, then cascade-reveal the hanging doors
    Timeline railAnim = new Timeline(new KeyFrame(Duration.millis(350),
        new KeyValue(rail.strokeDashOffsetProperty(), 0, Interpolator.EASE_OUT)
    ));
    railAnim.setOnFinished(e -> {
        for (int i = 0; i < hangItems.size(); i++) {
            final javafx.scene.Node item   = hangItems.get(i);
            final double            target = (item instanceof ImageView) ? 0.9 : 1.0;
            PauseTransition delay = new PauseTransition(Duration.millis(i * 28));
            delay.setOnFinished(ev -> {
                FadeTransition ft = new FadeTransition(Duration.millis(140), item);
                ft.setFromValue(0);
                ft.setToValue(target);
                ft.play();
            });
            delay.play();
        }
    });
    railAnim.play();
}


    public game.engine.cells.Cell[][] getBoardCells() {
    return game.getBoard().getBoardCells();
}

public int[] indexToRowColPublic(int index) {
    int row = index / Constants.BOARD_COLS;
    int col = index % Constants.BOARD_COLS;
    if (row % 2 == 1) col = Constants.BOARD_COLS - 1 - col;
    return new int[]{row, col};
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
    public void playTurn() throws Exception { game.playTurn(); }

    public void usePowerup() throws Exception { game.usePowerup(); }

    public Monster getCurrentMonster() { return game.getCurrent(); }

    public Monster getWinner() { return game.getWinner(); }

    public int getLastRoll() { return game.getLastRoll(); }
public void setCurrentMonsterLabel(Label label) {
    this.currentMonsterLabel = label;
}


public void setDiceImageView(ImageView imageView) {
    this.diceImageView = imageView;
}
public Card getLastCardDrawn()   { return Board.getLastCardDrawn(); }
public void clearLastCardDrawn() { Board.clearLastCardDrawn(); }
public String getLastCellType()  { return Board.getLastCellType(); }
public int getPlayerPosition()   { return game.getPlayer().getPosition(); }
public int getOpponentPosition() { return game.getOpponent().getPosition(); }
public int getPlayerEnergy()     { return game.getPlayer().getEnergy(); }
public int getOpponentEnergy()   { return game.getOpponent().getEnergy(); }


public void refreshBoard(GridPane board, NumberBinding cellSize) {
    for (int i = 0; i < Constants.BOARD_ROWS * Constants.BOARD_COLS; i++) {
       
        // use GameController's indexToRowCol (the visual one)
        int[] rowCol = indexToRowCol(i);
        Cell cell = game.getBoard().getBoardCells()[rowCol[0]][rowCol[1]];


        if (!(cell instanceof DoorCell)) continue;
        if (i == Constants.BOARD_COLS * Constants.BOARD_ROWS - 1) continue;


        StackPane stackPane = (StackPane) getNodeFromGrid(board, i);
        if (stackPane == null) continue;


        String tagId = "cell-image-" + i;
        for (javafx.scene.Node node : stackPane.getChildren()) {
            if (tagId.equals(node.getId())) {
                String imagePath = getImagePath(cell, i);
                if (imagePath != null) {
                    ((ImageView) node).setImage(
                        new Image(getClass().getResourceAsStream(imagePath))
                    );
                }
                break;
            }
        }
    }
}
public void refreshLandedCell(GridPane board, int position) {
    int[] rowCol = indexToRowCol(position);
    Cell cell = game.getBoard().getBoardCells()[rowCol[0]][rowCol[1]];
   
    if (!(cell instanceof DoorCell)) return;
    if (position == Constants.BOARD_COLS * Constants.BOARD_ROWS - 1) return;


    StackPane stackPane = (StackPane) getNodeFromGrid(board, position);
    if (stackPane == null) return;


    String tagId = "cell-image-" + position;
    for (javafx.scene.Node node : stackPane.getChildren()) {
        if (tagId.equals(node.getId())) {
            String imagePath = getImagePath(cell, position);
            if (imagePath != null) {
                ((ImageView) node).setImage(
                    new Image(getClass().getResourceAsStream(imagePath))
                );
            }
            break;
        }
    }
}
}




