package edu.szebo.ppke.survival;

import edu.szebo.ppke.survival.ReverseAStar.*;
import edu.szebo.ppke.survival.proto.Communication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Utils {
	
	public static int[] toIntArray(Integer[] data) {
		int[] result = new int[data.length];
		for(int i = 0; i < data.length; i ++)
			result[i] = data[i];
		return result;
	}

	public static String messageToNiceString(Communication.Message message) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    int width = message.getWidth();
        int height = message.getHeight();
	    sw.append("Player Id: " + message.getId() + " \n");
        sw.append("Player HP: " + message.getHp() + " \n");
	    sw.append("Map sizes: width: "+ width + " x height: "+ height  + "\n");
	    sw.append("Map in a matrix form: \n");

	    List<Integer> fields =message.getFieldsList();
        printMatrix(createMatrix(fields, width, height), pw);

        return sw.toString();
    }

    public static void printMatrix(int[][] matrix, PrintWriter writer) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                writer.printf("%5d", matrix[row][col]);
            }
            writer.println();
        }
    }

    public static int[][] createMatrix(List<Integer> fields, int width, int height) {
	    int[][] matrix = new int[width][height];


	    int index = 0;
        for (Iterator<Integer> iterator = fields.iterator(); iterator.hasNext(); index ++) {
            int y = (int)index/width;
            int x = index - y*width;
            matrix[x][y] = iterator.next();
        }
        return matrix;
    }
    
    public static int getDistance(FieldAStar start, FieldAStar target, int width, int height)
    {
    	int distance = 0;
    	AStarFactory factory = new AStarFactory(width, height);
    	
    	distance = factory.buildAStar(start.x, start.y).getPathTo(target).length;
    		
    	return distance;
    }
    
    public static FieldAStar[] toFieldArray(Set<FieldAStar> data)
    {
    	Object[] res = data.toArray();
    	
    	FieldAStar[] result = new FieldAStar[data.size()];
    	for(int i = 0; i < data.size(); i++)
    	{
    		result[i] = (FieldAStar) res[i];
    	}
    	return result;
    }
}
