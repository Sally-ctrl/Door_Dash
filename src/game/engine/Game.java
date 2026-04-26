package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.*;

public class Game {
	private Board board;
	private ArrayList<Monster> allMonsters; 
	private Monster player;
	private Monster opponent;
	private Monster current;
	
	public Game(Role playerRole) throws IOException {
		this.allMonsters = DataLoader.readMonsters();
		this.board = new Board(DataLoader.readCards());
		
		this.player = selectRandomMonsterByRole(playerRole);
		this.opponent = selectRandomMonsterByRole(playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER);
		this.current = player;
		ArrayList<Monster> stationed = new ArrayList<>(allMonsters);
        stationed.remove(player);
        stationed.remove(opponent);
		board.setStationedMonsters(stationed);
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
<<<<<<< HEAD
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

=======

	private Monster getCurrentOpponent(){
		return (current == player)?opponent:player;
	}

	private int rollDice(){
		return new Random().nextInt(6) + 1;
	}

	public void usePowerup() throws OutOfEnergyException{
		if(current.getEnergy()<Constants.POWERUP_COST){
			throw new OutOfEnergyException();
		}
		current.setEnergy(current.getEnergy()-Constants.POWERUP_COST);
		current.executePowerupEffect(getCurrentOpponent());
	}

	public void playTurn() throws InvalidMoveException{
		if(getCurrent().isFrozen()){
			getCurrent().setFrozen(false);
			switchTurn();
			return;
		}
		int roll = rollDice();
		board.moveMonster(current, roll, getCurrentOpponent());
		switchTurn();
	}

	private void switchTurn(){
		current=getCurrentOpponent();
	}

	private boolean checkWinCondition(Monster monster) {
    return monster.getPosition() == Constants.WINNING_POSITION 
        && monster.getEnergy() >= Constants.WINNING_ENERGY;
}

	Monster getWinner(){
		 return (checkWinCondition(current))?getCurrent():checkWinCondition(opponent)?getCurrentOpponent():null;
	}







>>>>>>> b0376d76ec4331b485bade92772006d5c46dd3b6
}
