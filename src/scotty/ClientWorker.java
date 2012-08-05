package scotty;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import scotty.model.Request;
import scotty.model.Response;
import scotty.transporter.GatewayTransporter;
import scotty.transporter.impl.DefaultGatewayTransporter;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Worker class handels clients http request. Signs and encrypts the request and
 * send it to the gateway.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class ClientWorker implements HttpHandler {

	/**
	 * Communicator to/from the Gateway.
	 */
	private GatewayTransporter transporter = new DefaultGatewayTransporter();

	@Override
	public void handle(HttpExchange e) throws IOException {
		try {
			Request request = createRequest(e);

			// sign request

			// encrypt request

			try {
				Response response = transporter.sendAndReceive(request);

				// Set headers
				for (Entry<String, List<String>> entry : response.getHeaders()) {
					for (String val : entry.getValue()) {
						e.getResponseHeaders().add(entry.getKey(), val);
					}
				}

				// Set responsecode, and length of responsebody TODO check if 0
				// - always chunked encoding, is ok
				long bodyLength = 0;
				if (response.getBody() == null
						|| response.getBody().length() == 0) {
					bodyLength = -1;
				}

				e.sendResponseHeaders(response.getCode(), bodyLength);

				// Set body
				if (bodyLength >= 0) {
					e.getResponseBody().write(response.getBody().getBytes());
				}
			} catch (Exception ex) {
				System.err.println("can't send http request to gateway: "
						+ ex.getMessage());
			}
		} finally {
			e.getResponseBody().close();
			e.getRequestBody().close();
			e.close();
		}
	}

	/**
	 * Creates a new {@link Request} for use in scotty using a
	 * {@link HttpExchange} from the client.
	 * 
	 * @param e
	 *            {@link HttpExchange} from client.
	 * @return {@link Request} to use in scotty.
	 */
	public Request createRequest(HttpExchange e) {
		Request r = new Request();

		InputStream bi = e.getRequestBody();
		Headers h = e.getRequestHeaders();
		String method = e.getRequestMethod();
		URI uri = e.getRequestURI();

		Set<Entry<String, List<String>>> headers = h.entrySet();

		r.setHeaders(headers);
		r.setMethod(method);
		r.setUri(uri.toString());

		try {
			String body = HttpUtils.readFromInputStream(bi);
			r.setBody(body);
		} catch (IOException e1) {
			System.err.println("Couldnt set body ");
			e1.printStackTrace();
		}

		return r;
	}
}
