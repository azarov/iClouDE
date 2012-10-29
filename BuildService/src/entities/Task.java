package entities;

import java.net.URI;

/**
 * This class contains entire information about task and building result.
 */
public class Task {
	private int id;
	private URI fullPathToZip;
	private String languageType;
	private TaskStatus status;
	private String compileParameters;
	private String compilator;
	
	private static IdProvider idProvider = new IdProvider();
	
	public Task(URI fullPathToZip) {
		super();
		this.fullPathToZip = fullPathToZip;
		id = idProvider.getNextId();
		status = TaskStatus.NOT_BUILDED;
	}

	public int getId() {
		return id;
	}

	public String getLanguageType() {
		return languageType;
	}

	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getCompileParameters() {
		return compileParameters;
	}

	public void setCompileParameters(String compileParameters) {
		this.compileParameters = compileParameters;
	}

	public String getCompilator() {
		return compilator;
	}

	public void setCompilator(String compilator) {
		this.compilator = compilator;
	}

	public URI getFullPathToZip() {
		return fullPathToZip;
	}
	
	
}
