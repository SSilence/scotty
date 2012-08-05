package scotty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.istack.internal.logging.Logger;
import com.sun.net.httpserver.HttpServer;

/**
 * Server for client side access. Acts as http proxy.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class ClientServer {
	private static Logger log = Logger.getLogger(ClientServer.class);

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
	public void startClientServer() throws Exception {
		InetSocketAddress addr = new InetSocketAddress(clientServerPort);
		HttpServer server = HttpServer.create(addr, 0);

		server.createContext("/", new ClientWorker());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		//

		// // initialise the HTTPS server
		// HttpsServer httpsServer = HttpsServer.create(addr, 0);
		// SSLContext sslContext = SSLContext.getInstance("TLS");
		//
		// // initialise the keystore
		// char[] password = "simulator".toCharArray();
		// KeyStore ks = KeyStore.getInstance("JKS");
		// InputStream fis = ClientServer.class
		// .getResourceAsStream("/scotty.keystore");
		// ks.load(fis, password);
		//
		// // setup the key manager factory
		// KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		// kmf.init(ks, password);
		//
		// // setup the trust manager factory
		// TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		// tmf.init(ks);
		//
		// // setup the HTTPS context and parameters
		// sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		// httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
		// public void configure(HttpsParameters params) {
		// try {
		// // initialise the SSL context
		// SSLContext c = SSLContext.getDefault();
		// SSLEngine engine = c.createSSLEngine();
		// params.setNeedClientAuth(false);
		// params.setCipherSuites(engine.getEnabledCipherSuites());
		// params.setProtocols(engine.getEnabledProtocols());
		//
		// // get the default parameters
		// SSLParameters defaultSSLParameters = c
		// .getDefaultSSLParameters();
		// params.setSSLParameters(defaultSSLParameters);
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// log.log(Level.SEVERE, "Failed to create HTTPS port");
		// }
		// }
		// });
		//
		// httpsServer.createContext("/", new ClientWorker());
		// httpsServer.setExecutor(Executors.newCachedThreadPool());
		// httpsServer.start();

		//
		// InetSocketAddress addr2 = new InetSocketAddress(clientServerPort);
		// HttpsServer httpsServer = HttpsServer.create(addr2, 0);
		//
		// SSLContext sslContext = SSLContext.getDefault();
		//
		// httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
		// public void configure(HttpsParameters params) {
		//
		// // get the remote address if needed
		// InetSocketAddress remote = params.getClientAddress();
		//
		// SSLContext c = getSSLContext();
		//
		// // get the default parameters
		// SSLParameters sslparams = c.getDefaultSSLParameters();
		//
		// params.setSSLParameters(sslparams);
		// }
		// });
		// httpsServer.createContext("/", new ClientWorker());
		// httpsServer.setExecutor(Executors.newCachedThreadPool());
		// httpsServer.start();

	}
}
