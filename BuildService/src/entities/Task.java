package entities;

import java.net.URI;
import java.util.Date;

/**
 * This class contains entire information about task and building result.
 */
public class Task {
	private String id;
	private URI fullPathToZip;
	private String languageType;
	private OperationType operation;
	private TaskStatus status;
	private String compileParameters;
	private String compilator;
	private String entryPointPath;
	private Date queuingTime;
	private Date complitaionStartTime;
	
	public Task(URI fullPathToZip) {
		super();
		this.fullPathToZip = fullPathToZip;
		id = IdProvider.getNextId();
		status = TaskStatus.NOT_BUILDED;
	}

	public String getId() {
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

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	public String getEntryPointPath() {
		return entryPointPath;
	}

	public void setEntryPointPath(String entryPointPath) {
		this.entryPointPath = entryPointPath;
	}

	public Date getQueuingTime() {
		return queuingTime;
	}

	public void setQueuingTime(Date queuingTime) {
		this.queuingTime = queuingTime;
	}

	public Date getComplitaionStartTime() {
		return complitaionStartTime;
	}

	public void setComplitaionStartTime(Date complitaionStartTime) {
		this.complitaionStartTime = complitaionStartTime;
	}
	
	
}
