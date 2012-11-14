package scotty.plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

import scotty.transformer.RequestTransformer;
import scotty.transformer.ResponseTransformer;
import scotty.util.UserAgentProvider;

/**
 * This plugin intercepts the request/response and does the transformation,
 * specified by {@link RequestTransformer} and {@link ResponseTransformer}.
 *
 * @author flo
 *
 */
public class TransformingProxyPlugin extends ProxyPlugin {

	private Logger log = Logger.getLogger(getPluginName());

	private RequestTransformer requestTransformer;

	private ResponseTransformer responseTransformer;

	private UserAgentProvider uaProvider = new UserAgentProvider();

	private String gatewayUrl;

	public TransformingProxyPlugin(RequestTransformer requestTransformer,
			ResponseTransformer responseTransformer, String gatewayUrl) {
		this.requestTransformer = requestTransformer;
		this.responseTransformer = responseTransformer;
		this.gatewayUrl = gatewayUrl;
	}

	protected TransformingProxyPlugin() {

	}

	@Override
	public String getPluginName() {
		return "TransformingProxyPlugin";
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient in) {
		return new Plugin(in);
	}

	private class Plugin implements HTTPClient {

		private HTTPClient in;

		public Plugin(HTTPClient in) {
			this.in = in;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {
			Response response = null;

			byte[] cryptedRequest = requestTransformer
					.transformRequest(request);

			// Build request, which will be sent to the gateway:
			HttpUrl url = request.getURL();
			request = new Request();
			request.setContent(cryptedRequest);

			HttpUrl gateway = new HttpUrl(gatewayUrl);
			request.setHeader("Host", gateway.getHost());
			request.setHeader("Accept-Charset",
					"ISO-8859-1,utf-8;q=0.7,*;q=0.3");
			request.setHeader("Accept-Encoding", "deflate");
			request.setHeader("Accept-Language",
					"de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");
			request.setHeader("Content-Length",
					Integer.toString(cryptedRequest.length));
			request.setHeader("User-Agent", uaProvider.getUserAgent());

			request.setMethod("POST");

			if ("https".equalsIgnoreCase(url.getScheme())) {
				request.setURL(createHttpsGatewayUrl(gatewayUrl));
			} else {
				request.setURL(gateway);
			}

			Response cryptedResponse = in.fetchResponse(request);

			response = responseTransformer.transformResponse(cryptedResponse
					.getContent());
			response.setRequest(request);

			return response;
		}

	}

	public HttpUrl createHttpsGatewayUrl(String gatewayUrl)
			throws MalformedURLException, URIException {
		HttpMethod method = new GetMethod(gatewayUrl);
		method.setQueryString("ssl=true");
		String url = method.getURI().getEscapedURI();

		return new HttpUrl(url);
	}
}
