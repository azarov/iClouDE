package taskManagement;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import com.sun.research.ws.wadl.Link;

import entities.BuildResult;
import entities.BuildStatus;
import entities.Task;
import entities.TaskStatus;

//TODO: to think about thread-safety of this class
public class TasksQueue {
	
	private static volatile TasksQueue instance = null;
	private LinkedList<Task> readyQueue;
	private LinkedList<Task> processingQueue;
	private LinkedList<Task> performedTasksQueue;
	private HashMap<Integer, BuildResult> results; //or HashMap<Int, List<BuildResult>> to save history of results
	
	private TasksQueue()
	{
		readyQueue = new LinkedList<Task>();
		processingQueue = new LinkedList<Task>();
		performedTasksQueue = new LinkedList<Task>();
		results = new HashMap<Integer, BuildResult>();
	}
	
	public static TasksQueue getInstance()
	{
		if (instance == null) {
			synchronized (TasksQueue.class) {
				if (instance == null) {
					instance = new TasksQueue();
				}
			}
		}
		return instance;
	}
	
	public void add(Task task)
	{
		readyQueue.add(task);
	}
	
	public Task getNext()
	{
		Task first = readyQueue.peek();
		
		if (first != null) {
			processingQueue.add(first);
			first.setStatus(TaskStatus.IN_PROCESS);			
		}
		
		return first;
	}
	
	public LinkedList<Task> getAllTasks()
	{
		LinkedList<Task> tasks = new LinkedList<Task>();
		for (Task task : readyQueue) {
			tasks.add(task);
		}
		
		for (Task task : processingQueue) {
			tasks.add(task);
		}
		
		for (Task task : performedTasksQueue) {
			tasks.add(task);
		}
		
		return tasks;
	}

	public void saveResult(BuildResult buildResult) throws KeyNotFoundException {
		
		if (buildResult == null) {
			throw new IllegalArgumentException();
		}
		
		Task task = findTaskWithId(processingQueue, buildResult.getTaskId());
		
		if (task == null) {
			throw new KeyNotFoundException("No tasks found with id "+buildResult.getTaskId()+". No results will be saved");
		}
		
		if (buildResult.getBuildStatus() == BuildStatus.SUCCESSFUL) {			
			task.setStatus(TaskStatus.BUILDED);
		}
		else if (buildResult.getBuildStatus() == BuildStatus.FAILED) {
			task.setStatus(TaskStatus.NOT_BUILDED);
		}
		
		performedTasksQueue.add(task);
		results.put(buildResult.getTaskId(), buildResult);
	}
	
	public void clearPerformedTaskQueue()
	{
		performedTasksQueue.clear();
	}
	
	private Task findTaskWithId(Collection<Task> tasks, int id)
	{
		for (Task t : tasks) {
			if (t.getId() == id) {
				return t;
			}
		}
		return null;
	}
	
}
