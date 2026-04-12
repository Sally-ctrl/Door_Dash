package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	

	private int stealEnergyfrom(Monster target){
		int amount = Constants.SCHEMER_STEAL;

		if (target.getEnergy()<amount){
			amount = target.getEnergy();
		}

		return amount ;
	}
}

