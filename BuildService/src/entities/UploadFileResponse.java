package entities;

public class UploadFileResponse {
	private boolean result;
	private String description;
	private String zipID;

	public UploadFileResponse(String zipID, boolean result) {
		super();
		this.result = result;
		this.zipID = zipID;
		this.description = "";
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getZipID() {
		return zipID;
	}
	
	
	
}
