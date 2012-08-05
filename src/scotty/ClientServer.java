package scotty;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * Server for client side access. Acts as http proxy.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class ClientServer {
	/**
	 * Port for client side proxy
	 */
	private int clientServerPort = 0;

	public ClientServer(int port) {
		this.clientServerPort = port;
	}

	public static void main(String[] args) throws Exception {
		ClientServer s = new ClientServer(8080);
		s.startClientServer();
	}

	/**
	 * Starts the clientside server.
	 * 
	 * @throws IOException
	 *             Anything wrong with io
	 */
	public void startClientServer() throws IOException {
		InetSocketAddress addr = new InetSocketAddress(clientServerPort);
		HttpServer server = HttpServer.create(addr, 0);

		server.createContext("/", new ClientWorker());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}
}
