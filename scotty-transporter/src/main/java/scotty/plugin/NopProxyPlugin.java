package scotty.plugin;

import java.io.IOException;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

/**
 * This {@link ProxyPlugin} just proxies the requests/responses does no
 * transformation.
 * 
 * @author flo
 * 
 */
public class NopProxyPlugin extends ProxyPlugin {
	@Override
	public String getPluginName() {
		return "NopProxyPlugin";
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
			Response response = in.fetchResponse(request);
			return response;
		}
	}
}
