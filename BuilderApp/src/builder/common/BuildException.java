package builder.common;

import java.io.File;

import entities.BuildResult;
import entities.BuildStatus;
import entities.Task;

public class BuildException extends Exception {
	private String msg;
	private Task task;
	private File log;
	
	public BuildException(String msg, Throwable cause, IBuilder builder) {
		super(msg, cause);
		this.task = builder.getTask();
		this.log = builder.getLogFile();
	}
	
	public BuildResult constructBuildResult(){
		BuildResult r = new BuildResult(task.getId(), BuildStatus.FAILED, msg);
		r.setLogs(log.toURI());
		r.setCompiledSourcesPath(null);
		return r;
	}
	
}
