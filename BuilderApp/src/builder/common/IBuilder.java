package builder.common;

//import java.util.concurrent.Future;
import java.io.File;
import java.util.concurrent.RunnableFuture;

import entities.BuildResult;
import entities.Task;
/**
 * The interface for a builder
 */
public interface IBuilder{
	//{
	Task getTask();              
	public File getLogFile();      
	public File getTaskFolder();
	public File getSrcFolder();
	//} -> remove from IBuilder -> Context?
	RunnableFuture<BuildResult> build();
	//void run(BuildResult r);
	
	
}
