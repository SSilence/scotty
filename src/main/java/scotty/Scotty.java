package scotty;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.owasp.webscarab.httpclient.HTTPClientFactory;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.plugin.CredentialManager;
import org.owasp.webscarab.plugin.CredentialManagerUI;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.ListenerSpec;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.ui.swing.CredentialRequestDialog;

import scotty.plugin.CryptingProxyPlugin;

import com.btr.proxy.search.ProxySearch;

/**
 * Main class. scotty, transporter of freedom.
 * 
 * Usage: scotty [-g <Gateway-URL>] [-p <Local-Port>]
 * 
 * 
 * @author flo
 * 
 */
public class Scotty {
	private static Logger log = Logger.getLogger(Scotty.class.getName());

	private JFrame parent = new JFrame();

	/**
	 * Use gateway - if false, scotty acts transparent as proxy.
	 */
	public static boolean useGateway = true;

	/**
	 * CLI to specify gateway.
	 */
	private String GATEWAY_CMDLINE_PARAM = "g";

	/**
	 * CLI to specify the local port
	 */
	private String LOCALPORT_CMDLINE_PARAM = "p";

	/**
	 * Default gateway url, if none is specified.
	 */
	private String defaultGatewayUrl = "http://localhost";

	/**
	 * gateway to use.
	 */
	public static String gatewayUrl;

	/**
	 * Local port of scotty.
	 */
	private String localPort;

	/**
	 * Local address, scotty binds to.
	 */
	private String localAddr = "127.0.0.1";

	/**
	 * Default local port.
	 */
	private String defaultLocalPort = "8008";

	/**
	 * Initialize Scotty.
	 * 
	 * @param args
	 *            Arguments
	 * @throws Exception
	 *             Exception.
	 */
	public static void main(String[] args) throws Exception {
		Scotty scotty = new Scotty();
		scotty.handleCommandline(args);

		scotty.init();
	}

	/**
	 * Parses the commandline and sets the configs.
	 * 
	 * @param args
	 *            args.
	 * @throws Exception
	 *             exc.
	 */
	private void handleCommandline(String[] args) throws Exception {
		Options opts = new Options();
		opts.addOption(GATEWAY_CMDLINE_PARAM, true, "URL of the Gateway");
		opts.addOption(LOCALPORT_CMDLINE_PARAM, true,
				"Local port, where scotty listens for requests");

		CommandLineParser cmd = new PosixParser();
		CommandLine line = cmd.parse(opts, args);

		gatewayUrl = line.getOptionValue(GATEWAY_CMDLINE_PARAM,
				defaultGatewayUrl);

		localPort = line.getOptionValue(LOCALPORT_CMDLINE_PARAM,
				defaultLocalPort);
		Preferences.setPreference("Proxy.listeners", localAddr + ":"
				+ localPort);
	}

	public void init() throws Exception {
		Preferences.setPreference("WebScarab.promptForCredentials", "true");

		Framework framework = new Framework();

		configureProxySettings();

		// FIXME conversations werden hier auf dem Filesystem gespeichert.
		// Sollten wenn dann auch verschluesselt werden.
		framework.setSession("FileSystem",
				new File(System.getProperty("java.io.tmpdir") + "/scotty"), "");
		CredentialManager cm = framework.getCredentialManager();

		CredentialManagerUI credentialRequestDialog = new CredentialRequestDialog(
				parent, true, cm);
		cm.setUI(credentialRequestDialog);

		loadMainPlugin(framework);

		Thread.currentThread().join();
	}

	/**
	 * Loads the main plugin {@link CryptingProxyPlugin}. Starts the local proxy
	 * server.
	 * 
	 * @param framework
	 *            WebScarab Framework.
	 */
	public void loadMainPlugin(Framework framework) {
		Proxy proxy = new Proxy(framework);
		framework.addPlugin(proxy);

		CryptingProxyPlugin cp = new CryptingProxyPlugin();
		proxy.addPlugin(cp);

		for (ListenerSpec spec : proxy.getProxies()) {
			proxy.addListener(spec);
		}
	}

	/**
	 * Configures the Proxy Settings to access the gatway, uses system settings
	 * by utilising proxy-vole.
	 */
	public void configureProxySettings() {
		try {
			ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
			ProxySelector myProxySelector = proxySearch.getProxySelector();

			ProxySelector.setDefault(myProxySelector);

			List<java.net.Proxy> proxies = ProxySelector.getDefault().select(
					new URI(gatewayUrl));
			if (proxies.size() > 0) {
				HTTPClientFactory factory = HTTPClientFactory.getInstance();
				for (java.net.Proxy p : proxies) {
					InetSocketAddress address = (InetSocketAddress) p.address();

					if (Type.HTTP == p.type()) {

						factory.setHttpProxy(address.getHostName(),
								address.getPort());
						Preferences
								.setPreference(
										"WebScarab.httpProxy",
										address.getHostName() + ":"
												+ address.getPort());
					} else if (Type.SOCKS == p.type()) {
						factory.setHttpsProxy(address.getHostName(),
								address.getPort());
						Preferences
								.setPreference(
										"WebScarab.httpsProxy",
										address.getHostName() + ":"
												+ address.getPort());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showConfirmDialog(parent,
					"Proxy could not be set! Try it manually",
					"Proxy not resolved", JOptionPane.WARNING_MESSAGE);

		}

	}

}