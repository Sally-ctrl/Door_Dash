package game.engine;
import game.engine.cards.Card;
import game.engine.dataloader.DataLoader;
import game.engine.monsters.Monster;

import java.io.IOException;
import java.util.*;

public class Game {
	private final Board board;
	private  ArrayList<Monster> allMonsters;
	private Monster player;
	private Monster opponent;
	private  Monster current;
	public Game(Role playerRole) throws IOException{
		ArrayList<Card> cardsFromCSV = DataLoader.readCards();
		this.board = new Board(cardsFromCSV);
		this.allMonsters = DataLoader.readMonsters();
		this.player = selectRandomMonsterByRole(playerRole);
		Role oppRole = (playerRole==Role.LAUGHER)?Role.SCARER:Role.LAUGHER;
		this.opponent = selectRandomMonsterByRole(oppRole);
		this.current = this.player;
		
		
	}
	public Monster getCurrent() {
		return current;
	}
	public void setCurrent(Monster current) {
		this.current = current;
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
	private Monster selectRandomMonsterByRole(Role role){
		ArrayList<Monster> matches = new ArrayList<Monster>();
		for(int i=0;i<this.allMonsters.size();i++){
			if(this.allMonsters.get(i).getRole()==role){
				matches.add(allMonsters.get(i));
			}
			
		}
		if(matches.size()!=0){
			Random rand = new Random();
			int choice = rand.nextInt(matches.size());
			Monster chosen = matches.get(choice);
			return chosen;
		}
		return null;
	}
}
