package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

public class MultiTasker extends Monster {
	private int normalSpeedTurns;
	
	public MultiTasker(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.normalSpeedTurns = 0;
	}

	public int getNormalSpeedTurns() {
		return normalSpeedTurns;
	}

	public void setNormalSpeedTurns(int normalSpeedTurns) {
		this.normalSpeedTurns = normalSpeedTurns;
	}
   public void executePowerupEffect(Monster opponentMonster){
	   this.setNormalSpeedTurns(2);
	}
	@Override
public void setEnergy(int energy) {
    int current = super.getEnergy();
    int delta = energy - current;
	
    super.setEnergy(current + (delta + Constants.MULTITASKER_BONUS));
}
@Override
	public void move(int distance){
		if(normalSpeedTurns==0){
			if(distance==1)
				return;
		 super.move(distance/2); }
		else{
			this.setNormalSpeedTurns(this.normalSpeedTurns-1);
			super.move(distance);
		}
		}
	}
