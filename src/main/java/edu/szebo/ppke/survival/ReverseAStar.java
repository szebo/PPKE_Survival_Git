package edu.szebo.ppke.survival;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import edu.szebo.ppke.survival.AStarFactory.Position;

public class ReverseAStar {
	
	public static final int COST = 1;
	
	static class FieldAStar{
		int heuristicCost;
		int finalCost;
		int x, y;
		FieldAStar parent;
		
		
		enum FieldType{
			PLAYER, FOOD, BLOCK, FREE
		}
		
		public FieldAStar(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		FieldType fType = FieldType.FREE;
		
		public FieldAStar(int x, int y, FieldType fType, int finalCost)
		{
			this.x = x;
			this.y = y;
			this.finalCost = finalCost;
			this.fType = fType;
		}
		
		@Override
		public String toString()
		{
			return "["+this.x+", "+this.y+"]";
		}
	}
	
	static class PositionAStar{
		int x, y;
		PositionAStar parent;
		int finalCost;
		
		public PositionAStar(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private int cwidth, cheight;
	private FieldAStar[][] map = null;
	private boolean closed[][];
	private PriorityQueue<FieldAStar> open = new PriorityQueue<FieldAStar>(
			(Object o1, Object o2) -> {
				FieldAStar f1 = (FieldAStar)o1;
				FieldAStar f2 = (FieldAStar)o2;
				
				return f1.finalCost<f2.finalCost?-1:f1.finalCost>f2.finalCost?1:0;
			});
	private int startx, starty;
	private boolean calcStarted = false;
	
	public ReverseAStar(int width, int height, int startx, int starty, Set<Position> blocked)
	{
		this.startx = startx;
		this.starty = starty;
		cwidth = width;
		cheight = height;
		map = new FieldAStar[cwidth][cheight];
		closed = new boolean[cwidth][cheight];
		
		for(int x = 0; x < cwidth; x++)
		{
			for(int y = 0; y < cheight; y++)
			{
				Position p = new Position(x,y);
				if(blocked.contains(p))
					map[x][y] = null;
				else
					map[x][y] = new FieldAStar(x,y);
			}
		}
		
		if(map[startx][starty] == null) throw new IllegalArgumentException("Start field cannot be a blocked field!");
		map[startx][starty].finalCost = 0;
	}
	
	private void checkAndUpdateCost(FieldAStar current, FieldAStar t, int cost)
	{
		if(t == null || closed[t.x][t.y]) return;
		int t_final_cost = t.heuristicCost+cost;
		
		boolean inOpen = open.contains(t);
		if(!inOpen || t_final_cost < t.finalCost)
		{
			t.finalCost = t_final_cost;
			t.parent = current;
			if(!inOpen)open.add(t);
		}
	}
	
	public synchronized void findPath(int targetx, int targety)
	{		
		for(int x = 0; x < cwidth; x++)
		{
			for(int y = 0; y < cheight; y++)
			{
				if(map[x][y] != null)
					map[x][y].heuristicCost = Math.abs(x-targetx)+Math.abs(y-targety);
			}
		}
		
		if(!calcStarted)
		{
			open.add(map[startx][starty]);
			calcStarted = true;
		}
		
		FieldAStar current;
		
		while(true)
		{
			current = open.poll();
			if(current == null)break;
			closed[current.x][current.y] = true;
			
			if(current.equals(map[targetx][targety])) return;
			
			FieldAStar t;
			if(current.x-1 > 0)
			{
				t = map[current.x-1][current.y];
				checkAndUpdateCost(current, t, current.finalCost+COST);
			}
			
			if(current.x+1 < map.length)
			{
				t = map[current.x+1][current.y];
				checkAndUpdateCost(current, t, current.finalCost+COST);
			}
			
			if(current.y-1 >= 0)
			{
				t = map[current.x][current.y-1];
				checkAndUpdateCost(current, t, current.finalCost+COST);
			}
			
			if(current.y+1 < map[0].length)
			{
				t = map[current.x][current.y+1];
				checkAndUpdateCost(current, t, current.finalCost+COST);
			}
		}
	}
	
	public PositionAStar[] getPathTo(FieldAStar target)
	{
		if(!closed[target.x][target.y])
			findPath(target.x, target.y);
		
		List<FieldAStar> pathList = new ArrayList<FieldAStar>();

		int parentCount = 0;
		FieldAStar destination = target;
		while(destination.parent.x != startx && destination.parent.y != starty)
		{
			parentCount++;
			pathList.add(destination);
			destination = destination.parent;
		}		
		
		PositionAStar[] path = new PositionAStar[parentCount];
		
		for(int i = 0; i < parentCount; i++)
			path[i] = new PositionAStar(pathList.get(i).x, pathList.get(i).y);
		
		return path;
	}
}
