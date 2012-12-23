package ws;

import java.net.URI;

public class BuildServiceProperties {

	public URI baseURI = URI.create("http://192.168.1.100:5555/build_service/rest");
	public URI getTaskURI = URI.create(baseURI + "/inner/get_task");
	public URI sendResultURI = URI.create(baseURI + "/inner/receive_result");

	
	private static BuildServiceProperties instance;
	private BuildServiceProperties() {}
	
	public static BuildServiceProperties buildServiceProperties() {
		if (instance == null) {
			instance = new BuildServiceProperties();
		}
		return instance;

	}
}
