package game.engine.cards;

import game.engine.monsters.Monster;

public class StartOverCard extends Card {
	public  StartOverCard(String name, String description, int rarity, boolean lucky){
		super( name,  description,  rarity, lucky);
		
	}
	public void performAction(Monster player, Monster opponent){
		
	}
	

}
