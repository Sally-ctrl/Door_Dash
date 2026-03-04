package game.engine.dataloader;
import java.io.*;
import game.engine.cards.*;
import java.util.ArrayList;
import game.engine.cells.*;

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
    		 String type = values[0];       
             String name = values[1];
             String description = values[2];
             int rarity = Integer.parseInt(values[3]);
             Card current;
             switch(type){
             case "SwapperCard":
                 current = new SwapperCard(name, description, rarity);
                 break;
             case "ShieldCard":
                 current = new ShieldCard(name, description, rarity);
                 break;
             case "EnergyStealCard":
                 int energy = Integer.parseInt(values[4]);
                 current = new EnergyStealCard(name, description, rarity, energy);
                 break;
             case "StartOverCard":
                 boolean lucky = Boolean.parseBoolean(values[4]);
                 current = new StartOverCard(name, description, rarity, lucky);
                 break;
             case "ConfusionCard":
                 int duration = Integer.parseInt(values[4]);
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
	 ArrayList<Card> cells= new ArrayList<>();
 }
}