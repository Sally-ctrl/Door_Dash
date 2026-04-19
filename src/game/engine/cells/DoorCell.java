package game.engine.cells;

import game.engine.Board;
import game.engine.Role;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;
import java.util.ArrayList;

public class DoorCell extends Cell implements CanisterModifier {
	private Role role;
	private int energy;
	private boolean activated;
	
	public DoorCell(String name, Role role, int energy) {
		super(name);
		this.role = role;
		this.energy = energy;
		this.activated = false;
	}
	
	public Role getRole() {
		return role;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean isActivated) {
		this.activated = isActivated;
	}

	public ArrayList<Monster> getCurrentTeam(Role role){
		ArrayList<Monster> team = new ArrayList<>();
		ArrayList<Monster> allMonsters = Board.getStationedMonsters();
		for(int i =0;i<allMonsters.size();i++){
			if(role == allMonsters.get(i).getRole()){
				team.add(allMonsters.get(i));
			}
		}
		return team;
	}
	public void modifyCanisterEnergy(Monster monster ,int canisterValue){
		if(this.activated){
			return;
		}
		ArrayList<Monster> team = getCurrentTeam(monster.getRole());
		boolean isMatch = monster.getRole()==this.role;
		int effect = isMatch?canisterValue:-canisterValue;
		if (!team.contains(monster)) {
        	team.add(monster);
    	}
		boolean consumed = false;
		for(int i =0;i<team.size();i++){
			boolean before = team.get(i).isShielded();
			team.get(i).alterEnergy(effect);
			boolean after = team.get(i).isShielded();
			if (before !=after )
				consumed= true;
			//gainedorLost = true;
		}
		if(!consumed){
			this.activated = true;
		}
	
	}

   public void onLand(Monster landingMonster,Monster opponetMonster){
     this.modifyCanisterEnergy(landingMonster, this.energy);


   }

	
}
