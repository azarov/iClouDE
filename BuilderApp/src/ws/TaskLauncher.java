package ws;

import static ws.BuildServiceProperties.buildServiceProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RunnableFuture;

import com.google.gson.Gson;

import entities.BuildResult;
import entities.Task;
import execution.Execution;

import builder.ant.AntBuilder;
import builder.common.BuildException;

/*
 * Gets task and ships it to executor
 * Runnable -> can execute in cycle
 */
public class TaskLauncher implements Runnable {

	private ExecutorService executor;
	
	public TaskLauncher(ExecutorService executor) {
		this.executor = executor;
	}
	
	@Override
	public void run() {
		String taskString = HttpMultiClient.INSTANCE
				.execGetRequest(buildServiceProperties().getTaskURI);
		Gson gson = new Gson();
		Task task = gson.fromJson(taskString, Task.class);
		RunnableFuture<BuildResult> buildProcess = null;
		TaskSender taskSender;
		try {
			buildProcess = new AntBuilder(task).build();
			taskSender = new TaskSender(buildProcess);
		} catch (BuildException e) {
			taskSender = new TaskSender(e.constructBuildResult());
		}
		taskSender.start();

		if (buildProcess != null){
			// to be executed in another thread
			executor.execute(buildProcess);
		}
	}

}
