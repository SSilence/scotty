package scotty;

import java.io.File;
import java.util.Properties;

import javax.swing.JFrame;

import org.mortbay.jetty.Server;
import org.mortbay.log.Log;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.plugin.CredentialManager;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.ListenerSpec;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.ui.swing.CredentialManagerFrame;
import org.owasp.webscarab.ui.swing.CredentialRequestDialog;

import scotty.plugin.CryptingProxyPlugin;
import scotty.plugin.LoggingProxyPlugin;

/**
 * Scotty main command line class.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class Scotty {
	public static Properties properties;

	private CredentialManagerFrame _credentialManagerFrame = null;
	private CredentialRequestDialog _credentialRequestDialog = null;
	private JFrame frame = new JFrame();

	public static String gatewayUrl = "http://localhost:9090/request";

	// Use gateway - if false, scotty acts transparent as proxy.
	public static boolean useGateway = false;

	public void init() throws Exception {
		Preferences.setPreference("WebScarab.promptForCredentials", "true");

		Framework framework = new Framework();

		setProxySettings();

		// FIXME conversations werden hier auf dem Filesystem gespeichert.
		// Sollten wenn dann auch verschluesselt werden.
		framework.setSession("FileSystem",
				new File(System.getProperty("java.io.tmpdir") + "/scotty"), "");
		CredentialManager cm = framework.getCredentialManager();
		_credentialManagerFrame = new CredentialManagerFrame(cm);
		_credentialRequestDialog = new CredentialRequestDialog(frame, true, cm);
		cm.setUI(_credentialRequestDialog);

		loadLitePlugins(framework);

		Thread.currentThread().join();
	}

	/**
	 * Configures the Proxy Settings.
	 */
	public void setProxySettings() {
		// TODO todo.
		// HTTPClientFactory _Factory = HTTPClientFactory.getInstance()
		// _factory.setHttpProxy(httpserver, httpport);
		// _factory.setHttpsProxy(httpsserver, httpsport);
		// _factory.setNoProxy(noproxies);
		//
		// Preferences.setPreference("WebScarab.httpProxy", httpserver + ":"
		// + httpport);
		// Preferences.setPreference("WebScarab.httpsProxy", httpsserver + ":"
		// + httpsport);
		// Preferences.setPreference("WebScarab.noProxy",
		// noProxyTextArea.getText());
		//

	}

	public void loadLitePlugins(Framework framework) {
		Log.debug("Loading Plugin ...");
		Proxy proxy = new Proxy(framework);
		framework.addPlugin(proxy);

		CryptingProxyPlugin cp = new CryptingProxyPlugin();
		proxy.addPlugin(cp);

		LoggingProxyPlugin me = new LoggingProxyPlugin();
		proxy.addPlugin(me);

		for (ListenerSpec spec : proxy.getProxies()) {
			proxy.addListener(spec);
		}
	}

	public static void main(String[] args) throws Exception {
		if (useGateway) {
			// Dummy Gateway, echoes only request.
			Server server = new Server(9090);
			server.setHandler(new DummyGatewayLoggerServer());
			server.start();
		}

		Scotty scotty = new Scotty();
		scotty.init();
	}
}