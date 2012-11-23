package entities;

public class NewTaskRequest {
	private int protocolVersion;
	private String zipID;
	private String languageType;
	private String operation;
	private String compileParameters;
	private String inputData;
	private String entryPointPath;
	private String compilator;
	
	public NewTaskRequest()
	{
		
	}
	
	public int getProtocolVersion() {
		return protocolVersion;
	}
	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	public String getZipID() {
		return zipID;
	}
	public void setZipID(String zipId) {
		this.zipID = zipId;
	}
	public String getLanguageType() {
		return languageType;
	}
	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getCompileParameters() {
		return compileParameters;
	}
	public void setCompileParameters(String compileParameters) {
		this.compileParameters = compileParameters;
	}
	public String getInputData() {
		return inputData;
	}
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}
	public String getEntryPointPath() {
		return entryPointPath;
	}
	public void setEntryPointPath(String entryPointPath) {
		this.entryPointPath = entryPointPath;
	}
	public String getCompilator() {
		return compilator;
	}
	public void setCompilator(String compilator) {
		this.compilator = compilator;
	}
	
	
}
