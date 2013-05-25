package scotty;

import org.apache.log4j.Logger;
import org.owasp.webscarab.httpclient.HTTPClientFactory;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.plugin.CredentialManager;
import org.owasp.webscarab.plugin.CredentialManagerUI;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.ListenerSpec;
import org.owasp.webscarab.plugin.proxy.Proxy;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;
import org.owasp.webscarab.ui.swing.CredentialRequestDialog;

import scotty.crypto.KeyManager;
import scotty.plugin.NopProxyPlugin;
import scotty.plugin.TransformingProxyPlugin;
import scotty.transformer.RequestTransformer;
import scotty.transformer.ResponseTransformer;
import scotty.transformer.cleartext.ClearTextRequestTransformer;
import scotty.transformer.cleartext.ClearTextResponseTransformer;
import scotty.transformer.impl.DefaultRequestTransformer;
import scotty.transformer.impl.DefaultResponseTransformer;
import scotty.ui.SystrayIndicatorProxyPlugin;
import scotty.ui.SystrayManager;
import scotty.util.Messages;

/**
 * scotty TODO doc
 * 
 * @author flo
 * 
 */
public class Scotty {

	private static Logger log = Logger.getLogger(Scotty.class);

	/**
	 * Key Manager for RSA Key Access
	 */
	private KeyManager keyManager = KeyManager.getInstance();

	/**
	 * WebScarab framework.
	 */
	private Framework framework;

	/**
	 * Manages systray access.
	 */
	private SystrayManager systray = new SystrayManager();

	/**
	 * Textmessages used in scotty.
	 */
	private Messages msgs = new Messages();

	/**
	 * Use gateway - if false, scotty acts transparent as proxy.
	 */
	private boolean useGateway = true;

	/**
	 * Use encryption - if false, scotty will not encrypt requests
	 */
	private boolean disableEncryption = false;

	/**
	 * gateway to use.
	 */
	private String gatewayUrl;

	/**
	 * Local port of scotty.
	 */
	private String localPort;

	/**
	 * Local address, scotty binds to.
	 */
	private String localAddr = "127.0.0.1";

	public void init() throws Exception {
		systray.setTooltip(msgs.scotty() + " " + gatewayUrl);

		framework = new Framework();

		Preferences.setPreference("WebScarab.promptForCredentials", "true");
		CredentialManager cm = framework.getCredentialManager();
		CredentialManagerUI credentialRequestDialog = new CredentialRequestDialog(
				null, true, cm);
		cm.setUI(credentialRequestDialog);

		loadMainPlugin(framework);

		Thread.currentThread().join();
	}

	/**
	 * Loads the main plugin {@link TransformingProxyPlugin}. Starts the local
	 * proxy server.
	 * 
	 * @param framework
	 *            WebScarab Framework.
	 */
	public void loadMainPlugin(Framework framework) {
		Proxy proxy = new Proxy(framework);
		framework.addPlugin(proxy);

		ProxyPlugin proxyPlugin = createProxyPlugin(useGateway,
				disableEncryption, keyManager, gatewayUrl);
		proxy.addPlugin(proxyPlugin);

		ProxyPlugin indicator = new SystrayIndicatorProxyPlugin(systray);
		proxy.addPlugin(indicator);

		for (ListenerSpec spec : proxy.getProxies()) {
			proxy.addListener(spec);
		}

		log.info("launching: "
				+ (useGateway ? "Using gateway: " + gatewayUrl : " Using no gateway")
				+ ", encryption: " + (disableEncryption ? "No" : "Yes")
				+ ", local proxy listening on: " + localAddr + ":" + localPort);
	}

	public ProxyPlugin createProxyPlugin(boolean useGateway,
			boolean disableEncryption, KeyManager keyManager, String gatewayUrl) {
		ProxyPlugin cp = null;
		if (useGateway) {
			RequestTransformer requestTransformer = createRequestTransformer(
					disableEncryption, keyManager);
			ResponseTransformer responseTransformer = createResponseTransformer(
					disableEncryption, keyManager);
			cp = new TransformingProxyPlugin(requestTransformer,
					responseTransformer, gatewayUrl);
		} else {
			cp = new NopProxyPlugin();
		}

		return cp;
	}

	public RequestTransformer createRequestTransformer(
			boolean disableEncryption, KeyManager keyManager) {
		if (disableEncryption) {
			return new ClearTextRequestTransformer();
		}
		return new DefaultRequestTransformer(keyManager);
	}

	public ResponseTransformer createResponseTransformer(
			boolean disableEncryption, KeyManager keyManager) {
		if (disableEncryption) {
			return new ClearTextResponseTransformer();
		}
		return new DefaultResponseTransformer(keyManager);
	}

	public void setHttpProxy(String host, Integer port) {
		HTTPClientFactory factory = HTTPClientFactory.getInstance();
		factory.setHttpProxy(host, Integer.valueOf(port));
		Preferences.setPreference("WebScarab.httpProxy", host + ":" + port);

		log.info("Using HTTP Proxy: " + host + ":" + port);
	}

	public void setHttpsProxy(String host, Integer port) {
		HTTPClientFactory factory = HTTPClientFactory.getInstance();
		factory.setHttpsProxy(host, Integer.valueOf(port));
		Preferences.setPreference("WebScarab.httpsProxy", host + ":" + port);

		log.info("Using HTTPS Proxy: " + host + ":" + port);
	}

	public void stop() {
		framework.stopPlugins();
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public void setKeyManager(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	public boolean isUseGateway() {
		return useGateway;
	}

	public void setUseGateway(boolean useGateway) {
		this.useGateway = useGateway;
	}

	public boolean isDisableEncryption() {
		return disableEncryption;
	}

	public void setDisableEncryption(boolean disableEncryption) {
		this.disableEncryption = disableEncryption;
	}

	public String getGatewayUrl() {
		return gatewayUrl;
	}

	public void setGatewayUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}

	public String getLocalPort() {
		return localPort;
	}

	public void setLocalPort(String localPort) {
		this.localPort = localPort;

		Preferences.setPreference("Proxy.listeners", localAddr + ":"
				+ localPort);

	}

	public String getLocalAddr() {
		return localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;

		Preferences.setPreference("Proxy.listeners", localAddr + ":"
				+ localPort);
	}
}
