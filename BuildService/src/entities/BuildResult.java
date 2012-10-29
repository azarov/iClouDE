package entities;

import java.net.URI;

/***
 * Any result of building project
 *
 */
public class BuildResult {
	private int taskId;
	private URI compiledSourcesPath;
	private URI logs;
	private BuildStatus buildStatus;
	private String message;
	
	public BuildResult(int taskId, BuildStatus status, String message)
	{
		this.taskId = taskId;
		this.buildStatus = status;
		this.message = message;
	}

	public int getTaskId() {
		return taskId;
	}



	public URI getCompiledSourcesPath() {
		return compiledSourcesPath;
	}

	public void setCompiledSourcesPath(URI compiledSourcesPath) {
		this.compiledSourcesPath = compiledSourcesPath;
	}

	public URI getLogs() {
		return logs;
	}

	public void setLogs(URI logs) {
		this.logs = logs;
	}

	public BuildStatus getBuildStatus() {
		return buildStatus;
	}

	public String getMessage() {
		return message;
	}
	
	 
}
