package scotty;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import scotty.fetcher.UrlFetcherFetcher;

public class StandaloneGatewayMain {

	public static void main(String[] args) throws Exception {
		GatewayServlet gatewayServlet = new GatewayServlet();
		gatewayServlet.setFetcher(new UrlFetcherFetcher());
		Server server = new Server(8008);
		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(gatewayServlet), "/*");
		server.start();
	}

}
