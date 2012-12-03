package ws;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

import static ws.BuildServiceProperties.buildServiceProperties;

import com.google.gson.Gson;

import entities.BuildResult;

//TODO wrap RunnableFuture in some interface
public class TaskSender extends Thread {

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
				e.printStackTrace(); // add logger
			}
		}
		String buildResultString = gson.toJson(buildResult, BuildResult.class);
		int statusCode = HttpMultiClient.INSTANCE.execPostRequest(
				buildResultString, "application/json",
				buildServiceProperties().sendResultURI);

		System.out.println("Answer from server is " +statusCode);

	}
}
