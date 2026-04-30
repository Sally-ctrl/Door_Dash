package game.engine.cells;
import game.engine.Constants;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class ContaminationSock extends TransportCell implements CanisterModifier{
	public  ContaminationSock(String name, int effect){
		super(name,-Math.abs(effect));
	}
	public void onLand(Monster landingMonster,Monster opponetMonster){ 
		super.onLand(landingMonster, opponetMonster);
	}
	public void modifyCanisterEnergy(Monster monster,int canisterValue){
		monster.alterEnergy(canisterValue);
	}
	public void transport(Monster monster){
		int temp = this.getEffect();
		modifyCanisterEnergy(monster, -Math.abs(Constants.SLIP_PENALTY));
		monster.setPosition(monster.getPosition()+temp);
	}
	
}
