package execution;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ws.HttpMultiClient;

import static ws.BuildServiceProperties.buildServiceProperties;

import com.google.gson.Gson;

import entities.BuildResult;

/**
 *  This class waits until the build process which is 
 *  a {@link java.util.concurrent.Future} is finished
 *  and sends obtained {@link entities.BuildResult} to server.
 * @author vitalii
 *
 */
public class TaskSender extends Thread {

	private static Logger logger = LogManager.getLogger(TaskSender.class.getName());

	private RunnableFuture<BuildResult> buildProcess;
	private BuildResult buildResult;

	public TaskSender(RunnableFuture<BuildResult> buildProcess) {
		this.buildProcess = buildProcess;
	}

	public TaskSender(BuildResult buildResult) {
		this.buildResult = buildResult;
	}
	
	@Override
	public void run() {
		Gson gson = new Gson();
		if (buildResult == null) {
			try {
				buildResult = buildProcess.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e.getMessage(), e);
			}
		}
		String buildResultString = gson.toJson(buildResult, BuildResult.class);
		logger.info(buildResultString);
		int statusCode = HttpMultiClient.INSTANCE.execPostRequest(
				buildResultString, "application/json",
				buildServiceProperties().sendResultURI);

		System.out.println("Answer from server is " +statusCode);

	}
}
