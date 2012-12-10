package utils;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulated work with properties.
 * This class is a singleton.
 * @author Andrew Azarov
 *
 */
public class PropertiesManager {

	private static Object lockObject = new Object();
	private static volatile PropertiesManager instance = null;
	
	private final Logger logger = LoggerFactory.getLogger(PropertiesManager.class);
	private static final String PathToConfig = "/conf.properties"; 
	private Properties properties;
	
	private PropertiesManager() {
		properties = new Properties();
		
		try {
			properties.load(this.getClass().getResourceAsStream(PathToConfig));
		} catch (IOException e) {
			logger.error("Can't load properties file", e);
		}
	}
	
	public static PropertiesManager getInstance()
	{
		if (instance == null) {
			synchronized (lockObject) {
				if (instance == null) {
					instance = new PropertiesManager();
				}
			}
		}
		return instance;
	}
	
	//all is OK. Property object is thread-safe
	public String getProperty(String propertyName)
	{
		return properties.getProperty(propertyName);
	}
	
	public String getProperty(String propertyName, String defaultValue)
	{
		return properties.getProperty(propertyName, defaultValue);
	}
}
