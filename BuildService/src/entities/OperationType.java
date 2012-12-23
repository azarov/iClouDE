package entities;

public enum OperationType {
	RUN("RUN"), BUILD("BUILD"), BUILD_AND_RUN("BUILD&RUN");
	
	private String code;

	private OperationType(String code) {
		this.code = code;
	}
	
	public static OperationType getOperationType(String type)
	{
		if(OperationType.RUN.code == type)
		{
			return OperationType.RUN;
		}
		else if(OperationType.BUILD.code == type) {
			return OperationType.BUILD;
		}
		else {
			return OperationType.BUILD_AND_RUN;
		}
	}
	
	public String getCode()
	{
		return code;
	}
}
