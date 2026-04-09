package game.engine.dataloader;
import java.io.*;
import game.engine.Role;
import game.engine.cards.*;
import java.util.ArrayList;
import game.engine.cells.*;
import game.engine.monsters.*;

public class DataLoader {
 public static final String CARDS_FILE_NAME ="cards.csv";
 public static final  String CELLS_FILE_NAME="cells.csv";
 public static final  String MONSTERS_FILE_NAME="monsters.csv";
  
 public static ArrayList<Card> readCards() throws IOException{
	ArrayList<Card> cards= new ArrayList<>();
    try(BufferedReader br= new BufferedReader(new FileReader(CARDS_FILE_NAME))){
    	String line;
    	while((line=br.readLine())!=null){
    		String[] values= line.split(",");
    		 String type = values[0].trim().toUpperCase();       
             String name = values[1].trim();
             String description = values[2].trim();
             int rarity = Integer.parseInt(values[3].trim());
             Card current;
             switch(type){
             case "SWAPPER":
                 current = new SwapperCard(name, description, rarity);
                 break;
             case "SHIELD":
                 current = new ShieldCard(name, description, rarity);
                 break;
             case "ENERGYSTEAL":
                 int energy = Integer.parseInt(values[4].trim());
                 current = new EnergyStealCard(name, description, rarity, energy);
                 break;
             case "STARTOVER":
                 boolean lucky = Boolean.parseBoolean(values[4].trim());
                 current = new StartOverCard(name, description, rarity, lucky);
                 break;
             case "CONFUSION":
                 int duration = Integer.parseInt(values[4].trim());
                 current = new ConfusionCard(name, description, rarity, duration);
                 break;
             default:
                 throw new IllegalArgumentException("Invalid card type " + type);

             }
             cards.add(current);
    	}
    }
    return cards;
}
 public static ArrayList<Cell> readCells() throws IOException{
	 ArrayList<Cell> cells= new ArrayList<>();
	 try(BufferedReader br= new BufferedReader(new FileReader(CELLS_FILE_NAME))){
	    	String line;
	    	while((line=br.readLine())!=null){
	    		String[] values= line.split(",");
	    		if(values.length==3){
	    			String name = values[0].trim();       
		            Role role =Role.valueOf(values[1].trim().toUpperCase());
		            int energy = Integer.parseInt(values[2].trim());
		            cells.add(new DoorCell(name,role,energy));
	    		}
	    		else if(values.length==2){
	    			String name=values[0];
	    			int effect=Integer.parseInt(values[1].trim());
	    			if(effect<0){
	    				 cells.add(new ContaminationSock(name,effect));
	    			}
	    			else if(effect>0){
	    				cells.add(new ConveyorBelt(name,effect));
	    			}
	    			else{
	    				throw new IllegalArgumentException("Invalid effect " + effect);

	    			}
	    		}
	    		 
	             }   	
	 }
	 return cells; 
	 
}
 public static ArrayList<Monster> readMonsters() throws IOException{
	 ArrayList<Monster> monsters= new ArrayList<>();
	 try(BufferedReader br= new BufferedReader(new FileReader(MONSTERS_FILE_NAME))){
		 String line;
	    	while((line=br.readLine())!=null){
	    		String[] values= line.split(",");
	    		String monsterType=values[0].trim().toUpperCase();
	    		String name=values[1].trim();
	    		String description=values[2];
	    		Role role= Role.valueOf(values[3].trim().toUpperCase());
	    		int energy = Integer.parseInt(values[4].trim());
	    		Monster current;
	    		switch(monsterType){
	    		case "DASHER":
	    			current = new Dasher(name,description,role,energy);
	    			break;
	    		case "DYNAMO":
	    			current = new Dynamo(name,description,role,energy);
	    			break;
	    		case "MULTITASKER":
	    			current = new MultiTasker(name,description,role,energy);
	    			break;
	    		case "SCHEMER":
	    			current = new Schemer(name,description,role,energy);
	    			break;
	    		default:
	                 throw new IllegalArgumentException("Invalid monster type " + monsterType );
	    		
	    		}
	    	monsters.add(current);	
	    	}    	
	 }
	 return monsters;
}

}