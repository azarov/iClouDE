package utils;

public class Protocol {

	private static final int Version = 1;
	
	/**
	 * Method checks correctness of version number
	 * @param version
	 * @return true if version is correct otherwise false
	 */
	public static boolean checkVersion(int version)
	{
		return version == Version;
	}
	
}
