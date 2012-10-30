package entities;

public class IdProvider {
	
	private static int id = 0;
	
	public static String getNextId()
	{
		return String.valueOf(id++);
	}
}
