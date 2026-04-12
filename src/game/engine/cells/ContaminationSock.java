package game.engine.cells;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class ContaminationSock extends TransportCell implements CanisterModifier{
	public  ContaminationSock(String name, int effect){
		super(name,-Math.abs(effect));
	}
	public void modifyCanisterEnergy(Monster monster,int canisterValue){
		monster.alterEnergy(canisterValue);
		//note: the value of Canister value will be calculated in 2.3
		//by using 2.3.1 onLand method 
	}
}
