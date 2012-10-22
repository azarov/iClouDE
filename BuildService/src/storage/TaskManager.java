package storage;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.NEW;

import entities.*;

public class TaskManager {

	
	private static TaskManager instance = null;
	private LinkedList<Task> queue;
	
	private TaskManager()
	{
		if (instance == null) {
			synchronized (TaskManager.class) {
				if (instance == null) {
					instance = new TaskManager();
				}
			}
		}
	}
	
	public static TaskManager getInstance()
	{
		return instance;
	}
	
	public void add(Task task)
	{
		queue.add(task);
	}
	
	public Task getNext()
	{
		return queue.pop();
	}
}
