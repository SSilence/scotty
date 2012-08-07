package scotty.plugin;

import java.io.IOException;
import java.util.logging.Logger;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

import scotty.Scotty;
import scotty.transformer.RequestTransformer;
import scotty.transformer.ResponseTransformer;
import scotty.transformer.impl.DefaultRequestTransformer;
import scotty.transformer.impl.DefaultResponseTransformer;



/**
 * This plugin does the encryption and sends to the gateway.
 * 
 * @author flo
 * 
 */
public class CryptingProxyPlugin extends ProxyPlugin {

	private Logger log = Logger.getLogger(getPluginName());

	private RequestTransformer requestTransformer = new DefaultRequestTransformer();

	private ResponseTransformer responseTransformer = new DefaultResponseTransformer();



	@Override
	public String getPluginName() {
		return "CryptingProxyPlugin";
	}



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

			byte[] cryptedRequest = requestTransformer.transformRequest(request);

			if (Scotty.useGateway) {
				// Build request, which will be sent to the gateway:

				request = new Request();
				request.setContent(cryptedRequest);
				request.setHeader("Content-Length", Integer.toString(cryptedRequest.length));
				request.setMethod("POST");
				if ("https".equalsIgnoreCase(request.getURL().getScheme())) {
					request.setURL(new HttpUrl(Scotty.gatewayUrl + "?ssl=true"));
				} else {
					request.setURL(new HttpUrl(Scotty.gatewayUrl));
				}
			}

			Response cryptedResponse = in.fetchResponse(request);

			if (Scotty.useGateway) {
				response = responseTransformer.transformResponse(cryptedResponse.getContent());
			} else {
				response = cryptedResponse;
			}

			return response;
		}
	}
}
