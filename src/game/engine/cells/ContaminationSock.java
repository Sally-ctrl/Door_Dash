package game.engine.cells;
import game.engine.Constants;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class ContaminationSock extends TransportCell implements CanisterModifier{
	public  ContaminationSock(String name, int effect){
		super(name,-Math.abs(effect));
	}
	public void onLand(Monster landingMonster,Monster opponetMonster){
		int canVal =  Constants.SLIP_PENALTY;
		this.transport(landingMonster);
		this.modifyCanisterEnergy(landingMonster,canVal);
	}
	public void modifyCanisterEnergy(Monster monster,int canisterValue){
		monster.alterEnergy(canisterValue);
		//note: the value of Canister value will be calculated in 2.3
		//by using 2.3.1 onLand method 
	}
	public void transport(Monster monster){
		int temp = this.getEffect()*-1; //i still want to check whether i will multiply by -1 or not bc in the constructor we already did 
		monster.move(temp);
	}
}
