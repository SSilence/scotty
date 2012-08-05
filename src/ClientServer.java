import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
	
	/**
	 * Starts the clientside server.
	 * 
	 * @throws IOException
	 *             Anything wrong with io
	 */
	public void startClientServer() throws IOException {
		ServerSocket serverSocket = new ServerSocket(clientServerPort);
		System.out.println("Server started on port " + clientServerPort);

		while (true) {
			// wait for next connection
			Socket clientSocket = serverSocket.accept();

			// start worker
			ClientWorker clientWorker = new ClientWorker(clientSocket);
			Thread worker = new Thread(clientWorker);
			worker.start();
		}
	}
}
