package game.engine.monsters;

import game.engine.Role;

public class Dynamo extends Monster {
	
	public Dynamo(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}

	public void executePowerupEffect(Monster opponentMonster) {
			opponentMonster.setFrozen(true);
	}

	@Override
	public void setEnergy(int energy){
		int current = super.getEnergy();
		int delta = energy - current;
		super.setEnergy(current+ delta*2);
	}

    @Override
	public void move(int distance){
		super.move(distance*2);
	}

		
}
