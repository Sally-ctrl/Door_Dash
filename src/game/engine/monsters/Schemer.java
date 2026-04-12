package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;
import java.util.ArrayList;

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
	public void executePowerupEffect(Monster opponentMonster) {
		
			int totalStolen = 0;
			int stolenFromOpponent = stealEnergyfrom(opponentMonster);
			totalStolen += stolenFromOpponent;
			ArrayList<Monster> team= getStationedMonsters();
			for(int i=0; i<team.size(); i++){
				Monster target = team.get(i);
				if(target != opponentMonster)
					int stolenFromTeammate = stealEnergyfrom(target);
					totalStolen += stolenFromTeammate;
				}
			this.alterEnergy(totalStolen);	
		}
		
	}
	
	




