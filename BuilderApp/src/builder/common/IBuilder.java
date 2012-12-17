package builder.common;

//import java.util.concurrent.Future;
import java.io.File;
import java.util.concurrent.RunnableFuture;

import entities.BuildResult;
import entities.Task;

/**
 * The interface for the builder
 * All builders should have "build" method
 * Other methods can be subjected to change or removal
 */
public interface IBuilder{

	Task getTask();              
	public File getLogFile();      
	public File getTaskFolder();
	public File getSrcFolder();

	RunnableFuture<BuildResult> build();
	
	
}
