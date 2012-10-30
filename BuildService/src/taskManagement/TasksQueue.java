package taskManagement;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import entities.BuildResult;
import entities.BuildStatus;
import entities.Task;
import entities.TaskStatus;

public class TasksQueue {
	
	private static volatile TasksQueue instance = null;
	
	private static Object lockObject = new Object();
	private Object saveResultLock = new Object(); 
	
	private Map<String, Task> queue;
	private ConcurrentHashMap<String, Task> performedTasks;
	private ConcurrentHashMap<String, BuildResult> results; //or HashMap<Int, List<BuildResult>> to save history of results
	
	private TasksQueue()
	{
		//getting synchronized map that maintains insertion order
		queue = Collections.synchronizedMap(new LinkedHashMap<String, Task>());
		performedTasks = new ConcurrentHashMap<>();
		results = new ConcurrentHashMap<String, BuildResult>();
	}
	
	public static TasksQueue getInstance()
	{
		if (instance == null) {
			synchronized (lockObject) {
				if (instance == null) {
					instance = new TasksQueue();
				}
			}
		}
		return instance;
	}
	
	public void add(Task task)
	{
		queue.put(task.getId(), task);
	}
	
	//O(n)
	public Task getNext()
	{
		Task first = null;
		
		synchronized (queue) {
			for (Entry<String, Task> entry : queue.entrySet()) {
				if (entry.getValue() != null && entry.getValue().getStatus() == TaskStatus.NOT_BUILDED) {
					first = entry.getValue();
					first.setStatus(TaskStatus.IN_PROCESS);
					break;
				}
			}
		}
		
		return first;
	}
	
	//O(n)
	public LinkedList<Task> getAllTasks()
	{
		LinkedList<Task> list = null;
		synchronized (queue) {
			list = new LinkedList<Task>(queue.values());
			
			synchronized (performedTasks) {
				list.addAll(performedTasks.values());
			}
			
		}
		return list;
	}

	//O(1)
	public void saveResult(BuildResult buildResult) throws KeyNotFoundException {
		
		if (buildResult == null) {
			//TODO: to log this fact
			//do nothing
			return;
		}
		
		synchronized (saveResultLock) {
			Task task = queue.get(buildResult.getTaskId());
			
			if (task == null) {
				throw new KeyNotFoundException("No tasks found with id "+buildResult.getTaskId()+". No results will be saved");
			}
			
			queue.remove(buildResult.getTaskId());
			
			setTaskRespectivelyBuildStatus(task, buildResult.getBuildStatus());
			
			performedTasks.put(task.getId(), task);
			results.put(buildResult.getTaskId(), buildResult);
		}
	}
	
	public void clearPerformedTaskQueue()
	{
		performedTasks.clear();
	}
	
	private void setTaskRespectivelyBuildStatus(Task task, BuildStatus buildStatus)
	{
		if (buildStatus == BuildStatus.SUCCESSFUL) {			
			task.setStatus(TaskStatus.BUILDED);
		}
		else if (buildStatus == BuildStatus.FAILED) {
			task.setStatus(TaskStatus.NOT_BUILDED);
		}
	}
	
}
