package scotty;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class Gateway {

	public static void main(String[] args) throws Exception {
		Server server = new Server(80);   
		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(new GatewayServlet()), "/*");
		server.start();
	}

}
