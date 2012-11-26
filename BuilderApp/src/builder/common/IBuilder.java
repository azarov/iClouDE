package builder.common;

//import java.util.concurrent.Future;
import java.io.File;
import java.util.concurrent.RunnableFuture;

import entities.BuildResult;
import entities.Task;
/*
 * IBuilder: getTask + void build()
 * 
 * cancelling: can't be done from outside
 * done: determined by script
 * 
 */
public interface IBuilder{
	Task getTask();
	public File getLogFile();
	public File getTaskFolder();
	public File getSrcFolder();
	
	RunnableFuture<BuildResult> build();
	
	
}
