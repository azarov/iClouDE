package ws;

public class HttpClientProperties{

	public int maxConnections = 100;
	

	private static HttpClientProperties instance;	
	private HttpClientProperties() {}

	public static HttpClientProperties httpClientProperties(){
		if (instance == null){
			instance = new HttpClientProperties();
		}
		return instance;
	}
}
