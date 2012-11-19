package entities;

import com.google.gson.Gson;

public class GsonProvider {
	
	private static Gson gson = new Gson();
	
	private GsonProvider()
	{
		
	}
	
	public static Gson getGson()
	{
		return gson;
	}
}
