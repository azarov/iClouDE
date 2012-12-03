package execution;

import static ws.BuildServiceProperties.buildServiceProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RunnableFuture;

import ws.HttpMultiClient;

import com.google.gson.Gson;

import entities.BuildResult;
import entities.Task;

import builder.ant.AntBuilder;
import builder.common.BuildException;

/**
 * The class fetches task from web service and passes it to executor
 * Also start a sender process which will send the result to server 
 * 
 * To sum up, the code in the run section starts entire task execution cycle
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
