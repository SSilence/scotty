package scotty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

/**
 * Gateway HTTP Server which only logs incoming requests and responses with OK
 * and the request content.
 * 
 * @author flo
 * 
 */
public class DummyGatewayLoggerServer extends AbstractHandler {
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		BufferedReader r = new BufferedReader(new InputStreamReader(
				request.getInputStream()));

		response.getWriter().println("Request:" + request.getRequestURI());

		String line = null;
		while ((line = r.readLine()) != null) {
			response.getOutputStream().write(line.getBytes());
		}

		response.flushBuffer();
		((Request) request).setHandled(true);

	}
}
