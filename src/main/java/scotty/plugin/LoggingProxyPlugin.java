package scotty.plugin;

import java.io.IOException;
import java.util.logging.Logger;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

/**
 * Plugin logs online the requested urls.
 * 
 * @author flo
 * 
 */
public class LoggingProxyPlugin extends ProxyPlugin {

	Logger log = Logger.getLogger(getPluginName());

	@Override
	public String getPluginName() {
		return "LoggingProxyPlugin";
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
			log.info("Get URL: " + request.getURL());

			Response response = in.fetchResponse(request);

			return response;
		}
	}
}
