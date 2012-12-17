package ws;


import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ws.HttpClientProperties.httpClientProperties;

/**
 * Http client that creates a connection pull to support
 * concurrent http requests
 * 
 * Can execute GET and POST requests to a given URI
 */
public enum HttpMultiClient {
	INSTANCE();

	private HttpClient httpClient;
	private static Logger logger = LogManager.getLogger(HttpMultiClient.class
			.getName());

	private HttpMultiClient() {
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(httpClientProperties().maxConnections);

		httpClient = new DefaultHttpClient(cm);

	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * HTTP GET request to server with given URI
	 * 
	 * @param answerCode HTTP answer code from server
	 * @return response as String
	 * @throws IOException
	 */
	public String execGetRequest(URI uri, Integer answerCode) throws  IOException {
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = null;
		response = httpClient.execute(httpGet, new BasicHttpContext());
		
		answerCode = response.getStatusLine().getStatusCode();
		try {
			HttpEntity entity = response.getEntity();
			return entity == null ? null : EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			httpGet.abort();
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * HTTP POST request to server with given URI
	 * 
	 * @param content request content
	 * @param contentType MIME-type of the request content
	 * @return HTTP answer code from server
	 */
	public int execPostRequest(String content, String contentType, URI uri) {
		HttpPost httpPost = new HttpPost(uri);
		StringEntity entity = null;
		try {
			entity = new StringEntity(content);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		entity.setContentType(contentType);
		httpPost.setEntity(entity);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);

			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				EntityUtils.consume(resEntity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.getStatusLine().getStatusCode();

	}
}
