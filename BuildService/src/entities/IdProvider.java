package entities;

public class IdProvider {
	
	private int id = 0;
	
	public int getNextId()
	{
		return id++;
	}
}
