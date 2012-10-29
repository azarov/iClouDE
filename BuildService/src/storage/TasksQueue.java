package storage;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.NEW;

import entities.*;

public class TasksQueue {
	
	private static volatile TasksQueue instance = null;
	private LinkedList<Task> queue;
	
	private TasksQueue()
	{
		queue = new LinkedList<Task>();
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
		queue.add(task);
	}
	
	public Task getNext()
	{
		return queue.pop();
	}
}
