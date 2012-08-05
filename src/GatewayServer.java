import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.impl.io.HttpRequestParser;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicLineParser;
import org.apache.http.params.BasicHttpParams;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Gateway
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class GatewayServer extends AbstractHandler {

	private int port;

	public GatewayServer(int port) {
		this.port = port;
	}

	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// get clients original http request
		String clientsRequest = baseRequest.getParameter("value");
		if (clientsRequest == null || clientsRequest.length() == 0) {
			return;
		}

		// decrypt request

		// check sign

		// get http request for client
		String forClientResponse = sendHttpRequest(clientsRequest);

		// sign response

		// encrypt response

		// send response to client
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				response.getOutputStream());
		outputStreamWriter.write(forClientResponse);
		outputStreamWriter.flush();
		
		response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}

	/**
	 * Starts the gateway (jetty webserver)
	 * 
	 * @throws Exception
	 */
	public void startGatewayServer() throws Exception {
		Server server = new Server(port);
		server.setHandler(this);
		server.start();
		server.join();
	}

	/**
	 * Executes clients request.
	 * 
	 * @param request
	 *            full http request as string
	 * @return response as string
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public String sendHttpRequest(String request) throws UnknownHostException,
			IOException, RuntimeException {

		// search for ssl connect (read host and port)
		// CONNECT ssl-account.com:443 HTTP/1.1

		// search for host and port
		// Host: www.test.de:920
		String[] parsedHostPort = parseHostPort(request);

		String host = "";
		int port = 80;
		if (parsedHostPort == null || parsedHostPort.length == 0) {
			throw new RuntimeException("can't read host and port");
		} else {
			if (parsedHostPort.length > 1) {
				host = parsedHostPort[1].trim();
			}
			if (parsedHostPort.length > 2) {
				port = Integer.parseInt(parsedHostPort[2].trim());
			}
		}

		// create tcp connection and get response
		Socket socket = new Socket(host, port);
		
		// send request
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
				socket.getOutputStream());
		outputStreamWriter.write(request);
		outputStreamWriter.flush();

		// fetch response
		String response = HttpUtils.readHttpRequestFromSocket(socket.getInputStream());
		
		socket.close();

		return response;
	}

	/**
	 * Parse hostname and port from request string.
	 * 
	 * @param request
	 *            Request String
	 * @return Array with Headername, Hostname and Port or null
	 */
	private String[] parseHostPort(String request) {
		StringTokenizer st = new StringTokenizer(request, "\n");

		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if (line.toLowerCase().indexOf("host:") != -1) {
				return line.split(":");
			}
		}

		return null;
	}
}
