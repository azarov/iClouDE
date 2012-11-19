package entities;

//TODO: change statuses
public enum TaskStatus {
	BUILDED("Building process has finished.", true), 
	NOT_BUILDED("Building process hasn't started yet.", false), 
	IN_PROCESS("Building process is in progress right now.", false);
	
	private String description;
	private boolean isTaskFinished;
	
	private TaskStatus(String description, boolean isTaskFinished)
	{
		this.description = description;
		this.isTaskFinished = isTaskFinished;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public boolean isTaskFinished()
	{
		return this.isTaskFinished;
	}
}
