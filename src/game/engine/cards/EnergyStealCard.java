package game.engine.cards;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class EnergyStealCard extends Card implements CanisterModifier {
	private int energy;

	public EnergyStealCard(String name, String description, int rarity, int energy) {
		super(name, description, rarity, true);
		this.energy = energy;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public void performAction(Monster player, Monster opponent){
		if(opponent.isShielded()){
			opponent.setShielded(false);
			return;
		}

		int amountToSteal = this.energy;

		if(opponent.getEnergy()<amountToSteal){
			amountToSteal=opponent.getEnergy();
		}

		if (amountToSteal <= 0) return;

		opponent.alterEnergy(-amountToSteal);
		player.alterEnergy(amountToSteal);
		/*modifyCanisterEnergy(opponent, -100);
			modifyCanisterEnergy(player, +100);
 			Bug:
		opponent shield might block its own negative effect
		BUT player still gains energy */
		
	}

	@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
    	monster.alterEnergy(canisterValue);
	}
}
