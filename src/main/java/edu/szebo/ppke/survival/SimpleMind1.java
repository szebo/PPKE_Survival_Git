package edu.szebo.ppke.survival;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.szebo.ppke.survival.ReverseAStar.*;
import edu.szebo.ppke.survival.ReverseAStar.FieldAStar.*;
import edu.szebo.ppke.survival.brain.Brain;
import edu.szebo.ppke.survival.proto.Communication;

public class SimpleMind1 implements Brain {
	
	private static Logger log = LoggerFactory.getLogger(SimpleMind1.class.getName());
	
    public int getWay(Communication.Message message) {
        
		log.info("Started thinking!");
		
		int way = 0;
		int id = 0;
		Set<FieldAStar> food = new HashSet<FieldAStar>(); 
		Set<FieldAStar> otherPlayers = new HashSet<FieldAStar>();
		
		boolean foundplayer = false;
		int playerx = -1, playery = -1;	
			try {
				id = message.getId();
				int height = message.getHeight();
				int width = message.getWidth();
				List<Integer> fields = message.getFieldsList();
				int[][] map = Utils.createMatrix(fields, width, height);
					
				AStarFactory factory = new AStarFactory(width, height);
				
				for(int x = 0; x < width; x++)
				{
					for(int y = 0; y < height; y++)
					{
						if(map[x][y] == 1000+id)
						{
							if(foundplayer)
							{
								log.error("Player found twice or more.");
							}
							else
							{
								playerx = x;
								playery = y;
							}
							
						}
						else if(map[x][y] == 1)
						{
							factory.setBlocked(x, y);
							log.info("Block found at: "+x+", "+y);
						}
						else if(10 < map[x][y] && map[x][y] < 100)
						{
							log.info("Food found at: "+x+", "+y);
							food.add(new FieldAStar(x, y, FieldType.FOOD, 0));
						}
						else if(map[x][y] > 1000 && map[x][y] != 1000+id)
						{
							log.info("Other player "+(map[x][y]-1000)+" found at: "+x+", "+y);
							otherPlayers.add(new FieldAStar(x, y, FieldType.PLAYER, 0));
						}
						else
						{
							log.info("Free field found at: "+x+", "+y);
						}
					}
				}
				
				if(food.isEmpty())
				{
					log.error("No food on map. Waiting for a new food?");
					way = 0;
				}
				else
				{	
					FieldAStar[] foodArray = Utils.toFieldArray(food);
					FieldAStar target = null;
					for(int i = 0; i < food.size(); i++)
					{
						if(Utils.getDistance(new FieldAStar(playerx, playery), foodArray[i], width, height) < Utils.getDistance(new FieldAStar(playerx, playery), target, width, height))
						{
							target = foodArray[i];
						}
					}
					for(int i = 0; i < food.size(); i++)
					{
						target.finalCost = factory.buildAStar(playerx, playery).getPathTo(target).length;
					}
					
					log.info("Picked random food.");
					ReverseAStar astar = factory.buildAStar(playerx, playery);
					ReverseAStar.PositionAStar[] path = astar.getPathTo(target);
					
					String wayToFood = "";
					for(int i = 0; i < target.finalCost; i++) { //destination.parent.x != playerx && destination.parent.y != playery
						wayToFood = "=>("+path[i].x+"," +path[i].y+ ")"+wayToFood;
						path[i] = path[i].parent;
					}
					log.info("The planned route is: " + wayToFood);
					// Destination is now where we' like to go next
					if(path[0].x > playerx)way = 3;//right
					else if(path[0].x < playerx)way = 1;//left
					else if(path[0].y > playery)way = 2;//up
					else if(path[0].y < playery)way = 4;//downs
					else way = 0;//nowhere
				}
				
			} 
			catch (Throwable e) {
				log.error("We failed with: "+e.getMessage(),e);
			}
		return way;
    }
    
    @Override
    public Communication.Answer decideBasedOn(Communication.Message message)
    {
    	int id = message.getId();
    	int way = getWay(message);
    	
    	return Communication.Answer.newBuilder()
		        .setId(id)
		        .setWay(way) // Always that way
		        .build();
    }
}
