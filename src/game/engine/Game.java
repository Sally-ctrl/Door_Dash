package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import game.engine.dataloader.DataLoader;
import game.engine.monsters.*;

public class Game {
	private Board board;
	private ArrayList<Monster> allMonsters; 
	private Monster player;
	private Monster opponent;
	private Monster current;
	
	public Game(Role playerRole) throws IOException {
		this.board = new Board(DataLoader.readCards());
		
		this.allMonsters = DataLoader.readMonsters();
		
		this.player = selectRandomMonsterByRole(playerRole);
		this.opponent = selectRandomMonsterByRole(playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER);
		this.current = player;
		allMonsters.remove(player);
		allMonsters.remove(opponent);
		board.setStationedMonsters(allMonsters);
	    board.initializeBoard(DataLoader.readCells());
	}
	
	public Board getBoard() {
		return board;
	}
	
	public ArrayList<Monster> getAllMonsters() {
		return allMonsters; 
	}
	
	public Monster getPlayer() {
		return player;
	}
	
	public Monster getOpponent() {
		return opponent;
	}
	
	public Monster getCurrent() {
		return current;
	}
	
	public void setCurrent(Monster current) {
		this.current = current;
	}
	
	private Monster selectRandomMonsterByRole(Role role) {
		Collections.shuffle(allMonsters);
	    return allMonsters.stream()
	    		.filter(m -> m.getRole() == role)
	    		.findFirst()
	    		.orElse(null);
	}
	private Monster getCurrentOpponent(){
		return current == player? opponent : player;
	}
	private int rollDice(){
		return ((int)(Math.random()*6))+1;
	}
	public void usePowerup() throws OutOfEnergyException{
		if(current.getEnergy<Constants.POWERUP COST){
			throw new OutOfEnergyException("Not enough energy to use powerup.");
		}
		else{
			current.alterEnergy(-Constants.POWERUP_COST);
			current.executePowerupEffect(getCurrentOpponent());
		}
	}
	public void playTurn() throws InvalidMoveException{
		if(current.isFrozen()){
			current.setFrozen(false);
			switchTurn();
			return;
		}
		else{
			int roll = this.rollDice();
			current.move(roll);
			switchTurn();
		}
	}
	private void switchTurn(){
		this.current=this.getCurrentOpponent();
	}
	private boolean checkWinCondition(Monster monster){
		return (monster.getEnergy()>=Constants.WINNING_ENERGY && monster.getPosition()==Constants.WINNING_POSITION) ;
	}
	public Monster getWinner(){
		if(checkWinCondition(player)){
			return player;
		}
		else if(checkWinCondition(opponent)){
			return opponent;
		}
		else{
			return null;
		}
	}

}
