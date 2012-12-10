package execution;

import static ws.BuildServiceProperties.buildServiceProperties;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RunnableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ws.HttpMultiClient;

import com.google.gson.Gson;

import entities.BuildResult;
import entities.Task;

import builder.ant.AntBuilder;
import builder.common.BuildException;

/**
 * The class fetches task from web service and passes it to executor Also start
 * a sender process which will send the result to server
 * 
 * To sum up, the code in the run section starts entire task execution cycle
 */
public class TaskLauncher implements Runnable {

	private ExecutorService executor;

	private static Logger logger = LogManager.getLogger(Execution.class
			.getName());

	public TaskLauncher(ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public void run() {
		Integer answer = -1;
		String taskString = null;
		try {
			taskString = HttpMultiClient.INSTANCE.execGetRequest(
					buildServiceProperties().getTaskURI, answer);
		} catch (IOException e1) {
			logger.error("Can't execute HTTP GET. Exiting..", e1);
			System.exit(1);
		}

		Gson gson = new Gson();
		Task task = gson.fromJson(taskString, Task.class);
		if (task == null) {
			logger.debug("No tasks yet..");
			return;
		}

		RunnableFuture<BuildResult> buildProcess = null;
		TaskSender taskSender;
		try {
			buildProcess = new AntBuilder(task).build();
			taskSender = new TaskSender(buildProcess);
		} catch (BuildException e) {
			taskSender = new TaskSender(e.constructBuildResult());
		}
		taskSender.start();

		if (buildProcess != null) {
			// to be executed in another thread
			executor.execute(buildProcess);
		}
	}

}
