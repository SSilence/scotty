package scotty;

import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

import javax.swing.JOptionPane;

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

import com.btr.proxy.search.ProxySearch;

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
	private KeyManager keyManager  = KeyManager.getInstance();

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

	/**
	 * Set if, proxy-vole should try to autoconfig the proxy. (If cmdline args
	 * are not used).
	 */
	private boolean autoConfigProxy = true;

	public void init() throws Exception {
		systray.setTooltip(msgs.scotty() + " " + gatewayUrl);

		configureProxySettings();
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

	/**
	 * Configures the Proxy Settings to access the gatway, uses system settings
	 * by utilising proxy-vole.
	 */
	public void configureProxySettings() {
		if (!autoConfigProxy)
			return;

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
						setHttpProxy(address.getHostName(), address.getPort());
					} else if (Type.SOCKS == p.type()) {
						setHttpsProxy(address.getHostName(), address.getPort());
					}
				}
			}
		} catch (Exception e) {
			log.warn(
					"Exception while trying to set proxy automatically via proxy-vole",
					e);
			JOptionPane
					.showMessageDialog(
							null,
							"Proxy could not be set! Try it manually via -proxyHost -proxyPort",
							"Proxy not resolved", JOptionPane.WARNING_MESSAGE);

		}

	}

	public void setHttpProxy(String host, Integer port) {
		HTTPClientFactory factory = HTTPClientFactory.getInstance();
		factory.setHttpProxy(host, Integer.valueOf(port));
		Preferences.setPreference("WebScarab.httpProxy", host + ":" + port);
	}

	public void setHttpsProxy(String host, Integer port) {
		HTTPClientFactory factory = HTTPClientFactory.getInstance();
		factory.setHttpsProxy(host, Integer.valueOf(port));
		Preferences.setPreference("WebScarab.httpsProxy", host + ":" + port);
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

	public boolean isAutoConfigProxy() {
		return autoConfigProxy;
	}

	public void setAutoConfigProxy(boolean autoConfigProxy) {
		this.autoConfigProxy = autoConfigProxy;
	}
}
