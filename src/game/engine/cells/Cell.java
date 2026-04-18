package game.engine.cells;

import game.engine.monsters.Monster;

public class Cell {
	private String name;
	private Monster monster;//representing the monster currently landed this cell 
	
	public Cell(String name) {
		this.name = name;
		this.monster = null;
	}

	public String getName() {
		return name;
	}
	
	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public void onLand(Monster landingMonster,Monster opponetMonster){
		this.monster = landingMonster; // track who landed
	}
	public boolean isOccupied(){
		if(this.monster==null)
			return false;
		else
			return true;
	}
}
