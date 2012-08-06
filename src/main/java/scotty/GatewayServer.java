package scotty;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

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

	@Override
	public void handle(String arg0, HttpServletRequest arg1,
			HttpServletResponse arg2, int arg3) throws IOException,
			ServletException {

//		// get clients original http request
//		String clientsRequest = arg1.getParameter("value");
//		if (clientsRequest == null || clientsRequest.length() == 0) {
//			return;
//		}
//
//		// decrypt request
//
//		// check sign
//
//		// get http request for client
//		String forClientResponse = sendHttpRequest(clientsRequest);
//
//		// sign response
//
//		// encrypt response
//
//		// send response to client
//		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
//				response.getOutputStream());
//		outputStreamWriter.write(forClientResponse);
//		outputStreamWriter.flush();
//
//		response.setStatus(HttpServletResponse.SC_OK);
//		baseRequest.setHandled(true);
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
		String response = HttpUtils
				.readFromInputStream(socket.getInputStream());

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
