package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import game.engine.cards.*;
import game.engine.dataloader.DataLoader;

public class Board {
	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters; 
	private static ArrayList<Card> originalCards;
	public static ArrayList<Card> cards;
	
	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		originalCards = readCards;
		cards = new ArrayList<Card>();
	}
	
	public Cell[][] getBoardCells() {
		return boardCells;
	}
	
	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}
	
	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static ArrayList<Card> getOriginalCards() {
		return originalCards;
	}
	
	public static ArrayList<Card> getCards() {
		return cards;
	}
	
	public static void setCards(ArrayList<Card> cards) {
		Board.cards = cards;
	}
	public static Card drawCard(){
		//will be implemented during game setup just a place holder for now for onLand() in CardCeLL class 
		return new ConfusionCard("", "", 0, 0); //bc it has to return i had to make it return smth ok?
	}
	private int[] indexToRowCol(int index){
		int row = index/10;
		int col = index %10;
		if(row%2==1){
			col = 9-col;
		}
		return new int[]{row,col};
	}
	private Cell getCell(int index){
		int[] ind = this.indexToRowCol(index);
		return boardCells[ind[0]][ind[1]];
	}
	 private void setCell(int index, Cell cell){
		 int[] ind = this.indexToRowCol(index);
		 this.boardCells[ind[0]][ind[1]] = cell;
	 }
	 public ArrayList<DoorCell> getDoorCells(ArrayList<Cell>specialCells){
		 ArrayList<DoorCell> doorArray = new ArrayList<DoorCell>();
		 for(int i=0;i<specialCells.size();i++){
			 if(specialCells.get(i) instanceof DoorCell){
				 doorArray.add((DoorCell) specialCells.get(i));
			 }
		 }
		 return doorArray;
	 }
	 public ArrayList<TransportCell> getTransportCells(ArrayList<Cell>specialCells){
		 ArrayList<TransportCell> transportArray = new ArrayList<TransportCell>();
		 for(int i=0;i<specialCells.size();i++){
			 if(specialCells.get(i) instanceof TransportCell){
				 transportArray.add((TransportCell) specialCells.get(i));
			 }
		 }
		 return transportArray;
	 }
	 public void initializeBoard(ArrayList<Cell> specialCells){
		 ArrayList<TransportCell> transportArray = getTransportCells(specialCells);
		 ArrayList<DoorCell> doorArray = getDoorCells(specialCells);
		 int j =0;
		 for(int i=0;i<100;i++){
			 if(i%2==0){
				 setCell(i,new Cell("Normal Cell"));
			 }
			 else{
				 setCell(i,doorArray.get(j));
				 j++;
			 }
		 }
		 ArrayList<ContaminationSock> contaminationArray= new ArrayList<>();
		 ArrayList<ConveyorBelt> conveyorArray= new ArrayList<>();
		 
		 for(int i=0;i<transportArray.size();i++){
			 if(transportArray.get(i) instanceof ContaminationSock){
				 contaminationArray.add((ContaminationSock)transportArray.get(i));
			 }
			 else{
				 conveyorArray.add((ConveyorBelt)transportArray.get(i));
			 }
			 
		 }
		 int[] sockPositions = Constants.SOCK_CELL_INDICES;
		 int[] conveyorPositions = Constants.CONVEYOR_CELL_INDICES;
		 for(int i=0; i<sockPositions.length;i++){
			 setCell(sockPositions[i],contaminationArray.get(i));
			 
		 }
		 for(int i=0; i<conveyorPositions.length;i++){
			 setCell(conveyorPositions[i],conveyorArray.get(i));
		 }
		 int[] cardPositions = Constants.CARD_CELL_INDICES;
		 //ArrayList<Card> cardArray=null;
		 //try{
		//	 cardArray = DataLoader.readCards();
		 //}
		 //catch (IOException e){
		//	 e.printStackTrace();
		 //}
		 for(int i=0; i<cardPositions.length;i++){
			 setCell(cardPositions[i],new CardCell("Card cell"));
		 }
		 ArrayList<Monster> stationed = Board.getStationedMonsters();
		 int[] cellMonsters = Constants.MONSTER_CELL_INDICES;
		 for(int i=0;i<cellMonsters.length;i++){
			 setCell(cellMonsters[i],new MonsterCell("Monster Cell",stationed.get(i)));
			 
		 }
		 
	 }
	 
	 private void setCardsByRarity(){
		 ArrayList<Card>  original= this.getOriginalCards();
		 originalCards = new ArrayList<Card>();
		 for(int i=0; i<original.size();i++){
			 int count = original.get(i).getRarity();
			 for(int j =0; j<count;j++){
				 Card current=original.get(i);
				 if(current instanceof ConfusionCard){
					 originalCards.add(new ConfusionCard(current.getName(),current.getDescription(),current.getRarity(),((ConfusionCard) current).getDuration())) ;
					 //cards.add(new ConfusionCard(current.getName(),current.getDescription(),current.getRarity(),((ConfusionCard) current).getDuration())) ;
						
				 }
				 else if(current instanceof EnergyStealCard){
					 originalCards.add(new EnergyStealCard(current.getName(),current.getDescription(),current.getRarity(),((EnergyStealCard)current).getEnergy())) ;
					 //cards.add(new EnergyStealCard(current.getName(),current.getDescription(),current.getRarity(),((EnergyStealCard)current).getEnergy())) ;
					 
				 }	
				 else if(current instanceof ShieldCard){
					 originalCards.add(new ShieldCard(current.getName(),current.getDescription(),current.getRarity())) ;
					 //cards.add(new ShieldCard(current.getName(),current.getDescription(),current.getRarity())) ;
					 
				 }
				 else if(current instanceof StartOverCard){
					 originalCards.add(new StartOverCard(current.getName(),current.getDescription(),current.getRarity(),current.isLucky())) ;
					 //cards.add(new StartOverCard(current.getName(),current.getDescription(),current.getRarity(),current.isLucky())) ;
					 
				 }
				 else if(current instanceof SwapperCard){
					 originalCards.add(new SwapperCard(current.getName(),current.getDescription(),current.getRarity())) ;
					 //cards.add(new SwapperCard(current.getName(),current.getDescription(),current.getRarity())) ;
				 }
				
			 }
		 }
	 }
	 
	 public  static void reloadCards(){
		 Random rand = new Random();
		 cards= new ArrayList<Card>(originalCards);
		 ArrayList<Card> temp= new ArrayList<Card>();

		 //helperLoad();
		 int currentSize = cards.size();
		 while(currentSize>0){
			 int j = rand.nextInt(currentSize);
			 Card currentCard = cards.get(j);
			 cards.remove(j);
			 currentSize--;
			 temp.add(currentCard);
		 }
		 cards = temp; 
		 
	 }
	 
	
}
