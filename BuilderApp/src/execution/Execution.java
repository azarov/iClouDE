package execution;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
//import org.apache.logging.log4j.spi.LoggerContext;

import ws.BuildServiceProperties;
import ws.HttpMultiClient;
import static execution.ExecutionProperties.executionProperties;

/**
 * Class with main method 
 * @author vitalii
 *
 */
public class Execution {

	private static Logger logger;// = LogManager.getLogger(Execution.class.getName());
	
	static{
		System.setProperty("log4j.configurationFile", "" +
				"/home/vitalii/workspace_backup/BuilderApp/src/log4j2.xml");
		logger = LogManager.getLogger(Execution.class.getName());
	}
	
	public static void main(String[] args){
		logger.debug("Start execution");
		ExecutorService executor = Executors.newFixedThreadPool(
				executionProperties().executionThreadsNumber);
		Timer timer = new Timer();
		final TaskLauncher taskLauncher = new TaskLauncher(executor);
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				taskLauncher.run();
			}
		}, 0, 3000L);
		
		
	}
}
