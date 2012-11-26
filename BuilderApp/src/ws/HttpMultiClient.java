package ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import entities.BuildResult;

import static ws.BuildServiceProperties.buildServiceProperties;
import static ws.HttpClientProperties.httpClientProperties;

/*
 * Singleton
 * or simply extend DefaultHttpClient
 */
public enum HttpMultiClient {
	INSTANCE();

	private HttpClient httpClient;

	// HttpGet host = new HttpGet(uri)

	private HttpMultiClient() {
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(httpClientProperties().maxConnections);

		httpClient = new DefaultHttpClient(cm);

	}

	public HttpClient getHttpClient() {
		return httpClient;
	}
	// or public int execGetRequest(URI uri, StringBuilder result) ?
	public String execGetRequest(URI uri) {
		HttpGet httpGet = new HttpGet(uri);
		try {
			HttpResponse response = httpClient.execute(httpGet,
					new BasicHttpContext());
			HttpEntity entity = response.getEntity();
			return entity == null ? null : EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			httpGet.abort();
			e.printStackTrace();
		}
		return null;
	}

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
