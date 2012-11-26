package execution;


public class ExecutionProperties {
	private static ExecutionProperties instance;
	
	public static ExecutionProperties executionProperties() {
		if (instance == null) {
			instance = new ExecutionProperties();
		}
		return instance;
	}
	
	public int executionThreadsNumber = 4;
	
}
