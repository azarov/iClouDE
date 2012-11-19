package entities;

public enum OperationType {
	RUN("RUN"), BUILD(""), BUILD_AND_RUN("BUILD&RUN");
	
	private String code;

	private OperationType(String code) {
		this.code = code;
	}
	
	public String getCode()
	{
		return code;
	}
}
