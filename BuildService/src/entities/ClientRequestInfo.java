package entities;

public class ClientRequestInfo {
	private int protocolVersion;
	private String zipID;
	
	public ClientRequestInfo()
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

	public void setZipID(String zipID) {
		this.zipID = zipID;
	}
	
	
	
	
}
