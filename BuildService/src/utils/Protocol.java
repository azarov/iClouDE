package utils;

public class Protocol {

	private static final int Version = 1;
	
	public static boolean checkVersion(int version)
	{
		return version == Version;
	}
}
