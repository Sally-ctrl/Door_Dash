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
    for(int i = 0; i < allMonsters.size(); i++){
        if(allMonsters.get(i).getRole().equals(role)){  
            team.add(allMonsters.get(i));
        }
    }
    return team;
}

@Override
public void modifyCanisterEnergy(Monster monster, int canisterValue) {
    int effect = monster.getRole().equals(this.role) ? canisterValue : -canisterValue;
    monster.alterEnergy(effect);
}

@Override
public void onLand(Monster landingMonster, Monster opponentMonster) {
    super.onLand(landingMonster, opponentMonster);

    if (this.activated)
        return;

    ArrayList<Monster> team = getCurrentTeam(landingMonster.getRole());
    boolean isSameRole = landingMonster.getRole().equals(this.role);

    if (isSameRole) {
        this.modifyCanisterEnergy(landingMonster, this.energy); 
        for (int i = 0; i < team.size(); i++)
            modifyCanisterEnergy(team.get(i), this.energy);
        this.activated = true;
    } else {
        if (landingMonster.isShielded()) {
            this.modifyCanisterEnergy(landingMonster, this.energy);
        } else {
            this.modifyCanisterEnergy(landingMonster, this.energy); 
            for (int i = 0; i < team.size(); i++)
                modifyCanisterEnergy(team.get(i), this.energy);
            this.activated = true;
        }
    }
}
    

        
}
