package execution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.filter.ThreadContextMapFilter;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
//import org.apache.logging.log4j.spi.LoggerContext;

import utils.Utils;
import ws.BuildServiceProperties;
import ws.HttpMultiClient;
import static execution.ExecutionProperties.executionProperties;

/**
 * Class with main method
 * 
 * @author vitalii
 * 
 */
public class Execution {

	private static Logger logger;

	static {
		InputStream is = ClassLoader.getSystemResourceAsStream("log4j2.xml");
		// folder for extracted resources
		File tmpDir = new File("tmp");
		if (tmpDir.exists()) {
			try {
				Utils.rm_rf(tmpDir);
			} catch (IOException e) {
				e.printStackTrace(); // no logger yet
			}
		}
		tmpDir.mkdir();
		File log4j2xml = new File("tmp/log4j2.xml");
		try {
			FileUtils.copyInputStreamToFile(is, log4j2xml);
		} catch (IOException e1) {
			logger.error("Could not extract log4j2.xml file", e1);
		}
		System.setProperty("log4j.configurationFile",
				log4j2xml.getAbsolutePath());
		// TODO set log file name in Java code, not in XML
		logger = LogManager.getLogger(Execution.class.getName());
	}

	public static void main(String[] args) {
		logger.debug("Start execution");
		ExecutorService executor = Executors
				.newFixedThreadPool(executionProperties().executionThreadsNumber);
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
