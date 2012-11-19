package entities;

public class Status {
	private boolean result;
	private String description;
	
	public Status(boolean result, String description) {
		this.result = result;
		this.description = description;
	}

	public boolean isResult() {
		return result;
	}
	public String getDescription() {
		return description;
	}
	
	
}
