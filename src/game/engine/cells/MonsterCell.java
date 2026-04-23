package game.engine.cells;

import game.engine.monsters.*;

public class MonsterCell extends Cell {
	private Monster cellMonster;

	public MonsterCell(String name, Monster cellMonster) {
		super(name);
		this.cellMonster = cellMonster;
	}

	public Monster getCellMonster() {
		return cellMonster;
	}

	public void onLand(Monster landingMonster,Monster opponentMonster){
		super.onLand(landingMonster,opponentMonster);
		if(landingMonster.getRole() == cellMonster.getRole() ){
			//landingMonster.executePowerupEffect(cellMonster);
			landingMonster.executePowerupEffect(opponentMonster);

			return;
		}
		
		int landing_Energy=landingMonster.getEnergy();
		int cell_Energy=cellMonster.getEnergy();
		boolean shielded =landingMonster.isShielded();
		
		if(landing_Energy>cell_Energy){
			if(shielded){
				cellMonster.setEnergy(landing_Energy);
				landingMonster.setShielded(false);
			}
			else{
				landingMonster.setEnergy(cell_Energy);
				cellMonster.setEnergy(landing_Energy);
			}
		}
		
	}

}
