import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Worker class handels clients http request. Signs and encrypts the request and
 * send it to the gateway.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class ClientWorker implements Runnable {

	/**
	 * Current client socket
	 */
	private Socket clientSocket;

	/**
	 * Constructor, sets the new client connection.
	 * 
	 * @param clientSocket
	 *            Client connection
	 */
	public ClientWorker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	/**
	 * Handels clients request.
	 */
	@Override
	public void run() {
		try {
			String request = HttpUtils.readHttpRequestFromSocket(clientSocket
					.getInputStream());

			// sign request

			// encrypt request

			// send request to server
			String response = "";
			try {
				response = sendHttpRequest(request);
			} catch (URISyntaxException e) {
				System.err.println("can't send http request to gateway: "
						+ e.getMessage());
			}

			// send response to client
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					clientSocket.getOutputStream());
			outputStreamWriter.write(response);
			outputStreamWriter.flush();
			outputStreamWriter.close();

			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends given string in parameter "value" to the given gateway and returns
	 * the response. Optionally proxy server will be used.
	 * 
	 * @param body
	 *            String for the Parameter "value"
	 * @return Response of the gateway
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	static public String sendHttpRequest(String body)
			throws URISyntaxException, ClientProtocolException, IOException {

		// Read config
		// todo: improve config handling
		String gatewayServer = Scotty.properties.getProperty("gatewayServer");
		String gatewayServerUrl = Scotty.properties
				.getProperty("gatewayServerUrl");
		String proxyHostname = Scotty.properties.getProperty("proxyHostname");
		int proxyPort = 8080;
		try {
			proxyPort = Integer.parseInt(Scotty.properties
					.getProperty("proxyPort"));
		} catch (Exception e) {
		}
		
		int gatewayPort = 8080;
		try {
			gatewayPort = Integer.parseInt(Scotty.properties
					.getProperty("gatewayPort"));
		} catch (Exception e) {
		}
		
		String proxyUser = Scotty.properties.getProperty("proxyUser");
		String proxyPassword = Scotty.properties.getProperty("proxyPassword");

		DefaultHttpClient httpclient = new DefaultHttpClient();

		// create post request
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("value", body));
		URIBuilder uriBuilder = new URIBuilder("http://" + gatewayServer + ":" + gatewayPort + "/"
				+ gatewayServerUrl);
		URI uri = uriBuilder.build();
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setEntity(new UrlEncodedFormEntity(qparams));

		// if proxy set, use proxy as host
		if (proxyHostname != null && proxyHostname.length() > 0) {
			HttpHost proxyHost = new HttpHost(proxyHostname, proxyPort);
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxyHost);
		}

		// set proxy user and password
		if (proxyUser != null && proxyUser.length() > 0
				&& proxyPassword != null && proxyPassword.length() > 0) {
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(proxyHostname, proxyPort),
					new UsernamePasswordCredentials(proxyUser, proxyPassword));
		}

		// execute http request
		HttpHost targetHost = new HttpHost(gatewayServer, gatewayPort);
		HttpResponse response = httpclient.execute(targetHost, httpPost);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}
}
