package edu.szebo.ppke.survival;

import java.util.Set;
import java.util.TreeSet;

public class AStarFactory {
	
	static class Position{
		int x, y;
		
		public Position(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	private int width, height;
	private Set<Position> blocked = new TreeSet<Position>((Object o1, Object o2) -> {
		Position p1 = (Position)o1;
		Position p2 = (Position)o2;
		
		if(p1.x<p2.x) return -1;
		else if(p1.x>p2.x)return 1;
		else if(p1.y <p2.y)return -1;
		else if(p1.y>p2.y)return 1;
		else return 0;
		}); 
	
	public AStarFactory(int width, int height)
	{
		this.width = width;
		this.height= height;
	}
	
	public void setBlocked(int x, int y)
	{
		blocked.add(new Position(x,y));
	}
	
	public ReverseAStar buildAStar(int startx, int starty)
	{
		return new ReverseAStar(width, height, startx, starty, blocked);
	}
	
}
