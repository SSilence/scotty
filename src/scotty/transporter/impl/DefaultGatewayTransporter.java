package scotty.transporter.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import scotty.Scotty;
import scotty.model.Request;
import scotty.model.Response;
import scotty.transporter.GatewayTransporter;
import scotty.transporter.transformer.RequestTransformer;
import scotty.transporter.transformer.ResponseTransformer;
import scotty.transporter.transformer.impl.DefaultRequestTransformer;
import scotty.transporter.transformer.impl.GatewayResponseTransformer;

/**
 * Default Transporter packs a {@link Request} into a format via a
 * {@link RequestTransformer} and sends it to the gateway, the response from the
 * gateway gets transformed via {@link ResponseTransformer}.
 * 
 * @author flo
 * 
 */
public class DefaultGatewayTransporter implements GatewayTransporter {
	private RequestTransformer requestTransformer = new DefaultRequestTransformer();
	private ResponseTransformer responseTransformer = new GatewayResponseTransformer();

	@Override
	public Response sendAndReceive(Request request) throws Exception {

		// Transform request to a gateway-complaint format.
		String transformedRequest = requestTransformer
				.transformRequest(request);

		// Read config
		// TODO improve config handling
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
		qparams.add(new BasicNameValuePair("value", transformedRequest));
		URIBuilder uriBuilder = new URIBuilder("http://" + gatewayServer + ":"
				+ gatewayPort + "/" + gatewayServerUrl);
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
		String rawResponse = EntityUtils.toString(entity);

		// Convert response from gateway to known Response obj.
		Response transformedResponse = responseTransformer
				.transformResponse(rawResponse);

		return transformedResponse;
	}
}
