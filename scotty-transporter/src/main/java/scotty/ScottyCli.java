package scotty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;
import scotty.event.EventDispatcher;
import scotty.event.EventObserver;
import scotty.event.Events;

/**
 * Main class CLI facade. scotty, transporter of freedom.
 * 
 * Usage: scotty [-g <Gateway-URL>] [-p <Local-Port>] [ -d ] [ -proxyHost
 * <proxyHost> -proxyPort <Port> ]<br />
 * <br />
 * CLI Params: <br />
 * <br />
 * -g Gateway-url wg. http://my.gatew.ay/gate.php <br />
 * -p local Port eg. 8008 <br />
 * -d disables gateway usage, no value<br />
 * -proxyHost proxy Host (eg. my.pro.xy)<br />
 * -proxyPort proxy Port (eg. 8080)
 * 
 * @author flo
 * 
 */
public class ScottyCli implements EventObserver {

	private static Logger log = Logger.getLogger(ScottyCli.class);

	/**
	 * CLI to disable the gateway usage
	 */
	private static final String DONT_USE_GATEWAY = "d";

	/**
	 * CLI to specify gateway.
	 */
	private static final String GATEWAY_CMDLINE_PARAM = "g";

	/**
	 * CLI to specify the local port
	 */
	private static final String LOCALPORT_CMDLINE_PARAM = "p";

	/**
	 * CLI for creating a new key pair
	 */
	private static final String CREATEKEY_CMDLINE_PARAM = "c";

	/**
	 * CLI for proxy host
	 */
	private static final String PROXY_HOST_CMDLINE_PARAM = "proxyHost";

	/**
	 * CLI for proxy port
	 */
	private static final String PROXY_PORT_CMDLINE_PARAM = "proxyPort";

	/**
	 * CLI for creating a new key pair
	 */
	private static final String TOKENTIMEOUT_CMDLINE_PARAM = "timeout";

	/**
	 * CLI to specify gateway.
	 */
	private static final String DISABLE_ENCRYPTION_CMDLINE_PARAM = "disableencryption";

	/**
	 * CLI for private key
	 */
	private static final String PRIVATEKEY_CMDLINE_PARAM = "privatekey";

	/**
	 * CLI for public key
	 */
	private static final String PUBLICKEY_CMDLINE_PARAM = "publickey";

	/**
	 * CLI for gateways public key
	 */
	private static final String GATEWAYSPUBLICKEY_CMDLINE_PARAM = "gatewayspublickey";
	
	/**
	 * CLI password for private key
	 */
	private static final String PRIVATEKEYPASS_CMDLINE_PARAM = "privatekeypassword";

	/**
	 * default private key
	 */
	private static final String DEFAULT_PRIVATEKEY = "resources:defaultprivatekey";

	/**
	 * default public key
	 */
	private static final String DEFAULT_PUBLICKEY = "resources:defaultpublickey";

	/**
	 * default gateways key
	 */
	private static final String DEFAULT_GATEWAYSPUBLICKEY = "resources:gatewaydefaultpublickey";

	/**
	 * Instance of scotty.
	 */
	private Scotty scotty;

	/**
	 * Default local port.
	 */
	private String defaultLocalPort = "8008";

	public ScottyCli() {
		EventDispatcher.add(this);
	}

	/**
	 * Initialize Scotty.
	 * 
	 * @param args
	 *            Arguments
	 * @throws Exception
	 *             Exception.
	 */
	public static void main(String[] args) throws Exception {
		ScottyCli cli = new ScottyCli();
		Scotty scotty = new Scotty();
		cli.scotty = scotty;

		CommandLine commandLine = cli.handleCommandline(args);

		// generate keys
		if (commandLine.hasOption(CREATEKEY_CMDLINE_PARAM)) {
			generateKeyPair();

			// start scotty
		} else {

			// load keys
			cli.loadKeys(commandLine);

			// start server
			scotty.init();
		}

	}

	private static void generateKeyPair() throws IOException, Exception,
			CryptoException {
		BufferedReader commandLineInput = new BufferedReader(
				new InputStreamReader(System.in));
		System.out.print("filename of private key: ");
		String privateKeyFile = commandLineInput.readLine();

		System.out.print("filename of public key: ");
		String publicKeyFile = commandLineInput.readLine();

		System.out.print("password for private key: ");
		String privateKeyPassword = new String(System.console().readPassword());

		KeyManager keyManager = KeyManager.getInstance();
		keyManager.generateKeyPair();
		System.out.println("key pair successfully generated");

		keyManager.writePrivateKey(privateKeyFile, privateKeyPassword);
		System.out.println("private key successfully saved");

		keyManager.writePublicKey(publicKeyFile);
		System.out.println("public key successfully saved");

		// check parsing files
		KeyManager checkKeyManager = KeyManager.getInstance();
		checkKeyManager.readPrivateKey(privateKeyFile, privateKeyPassword);
		if (checkKeyManager.getPrivateKey() != null)
			System.out.println("private key successfully read");
		else
			System.err.println("can't read generated private key file");

		checkKeyManager.readPublicKey(publicKeyFile);
		if (checkKeyManager.getPublicKey() != null)
			System.out.println("public key successfully read");
		else
			System.err.println("can't read generated public key file");
	}

	/**
	 * Load Keys for RSA Encryption, either from commandline or default keys.
	 * 
	 * @param commandLine
	 * @throws CryptoException
	 */
	public void loadKeys(CommandLine commandLine) throws CryptoException {
		KeyManager keyManager = KeyManager.getInstance();

		if (commandLine.hasOption(PRIVATEKEY_CMDLINE_PARAM)) {
			String password = commandLine.hasOption(PRIVATEKEYPASS_CMDLINE_PARAM) ? commandLine.getOptionValue(PRIVATEKEYPASS_CMDLINE_PARAM) : "";
			if(password==null) {
				System.out.print("password for private key: ");
				password = new String(System.console().readPassword());
			}
			keyManager.readPrivateKey(commandLine.getOptionValue(PRIVATEKEY_CMDLINE_PARAM), password);
			log.info("Use given private key " + commandLine.getOptionValue(PRIVATEKEY_CMDLINE_PARAM));
		} else {
			keyManager.readPrivateKey(DEFAULT_PRIVATEKEY, "");
		}

		if (commandLine.hasOption(PUBLICKEY_CMDLINE_PARAM)) {
			keyManager.readPublicKey(commandLine.getOptionValue(PUBLICKEY_CMDLINE_PARAM));
			log.info("Use given public key " + commandLine.getOptionValue(PUBLICKEY_CMDLINE_PARAM));
		} else {
			keyManager.readPublicKey(DEFAULT_PUBLICKEY);
		}
		if (commandLine.hasOption(GATEWAYSPUBLICKEY_CMDLINE_PARAM)) {
			keyManager.readGatewaysPublicKey(commandLine.getOptionValue(GATEWAYSPUBLICKEY_CMDLINE_PARAM));
			log.info("Use given gateway public key " + commandLine.getOptionValue(GATEWAYSPUBLICKEY_CMDLINE_PARAM));
		} else {
			keyManager.readGatewaysPublicKey(DEFAULT_GATEWAYSPUBLICKEY);
		}
		if (commandLine.hasOption(TOKENTIMEOUT_CMDLINE_PARAM)) {
			long tokenTimeout = Long.parseLong(commandLine.getOptionValue(TOKENTIMEOUT_CMDLINE_PARAM));
			keyManager.setMaxValidityOfToken(tokenTimeout);
			log.info("Use given token timeout " + commandLine.getOptionValue(TOKENTIMEOUT_CMDLINE_PARAM));
		}

	}

	/**
	 * Parses the commandline and sets the configs.
	 * 
	 * @param args
	 *            args.
	 * @throws Exception
	 *             exc.
	 */
	private CommandLine handleCommandline(String[] args) throws Exception {
		Options opts = new Options();
		opts.addOption(GATEWAY_CMDLINE_PARAM, true, "URL of the Gateway");
		opts.addOption(LOCALPORT_CMDLINE_PARAM, true,
				"Local port, where scotty listens for requests");
		opts.addOption(CREATEKEY_CMDLINE_PARAM, false, "Create new KeyPair");
		opts.addOption(PRIVATEKEY_CMDLINE_PARAM, true, "private key");
		opts.addOption(OptionBuilder.hasOptionalArg().withDescription("private key password (if one was set)").create(PRIVATEKEYPASS_CMDLINE_PARAM));
		opts.addOption(PUBLICKEY_CMDLINE_PARAM, true, "public key");
		opts.addOption(GATEWAYSPUBLICKEY_CMDLINE_PARAM, true, "gateways public key");
		
		opts.addOption(DISABLE_ENCRYPTION_CMDLINE_PARAM, false,
				"disable encryption");
		opts.addOption(TOKENTIMEOUT_CMDLINE_PARAM, true,
				"timeout for the RSA token (default = 1.5 hours)");

		opts.addOption(DONT_USE_GATEWAY, false,
				"Don't use gateway - direct connection.");
		opts.addOption(PROXY_HOST_CMDLINE_PARAM, true, "Proxy Host");
		opts.addOption(PROXY_PORT_CMDLINE_PARAM, true, "Proxy Port");

		CommandLineParser cmd = new PosixParser();
		CommandLine line = cmd.parse(opts, args);
		if (line.hasOption(PROXY_HOST_CMDLINE_PARAM)
				&& line.hasOption(PROXY_PORT_CMDLINE_PARAM)) {
			scotty.setHttpProxy(line.getOptionValue(PROXY_HOST_CMDLINE_PARAM),
					Integer.valueOf(line
							.getOptionValue(PROXY_PORT_CMDLINE_PARAM)));
			scotty.setHttpsProxy(line.getOptionValue(PROXY_HOST_CMDLINE_PARAM),
					Integer.valueOf(line
							.getOptionValue(PROXY_PORT_CMDLINE_PARAM)));
		}

		scotty.setGatewayUrl(line.getOptionValue(GATEWAY_CMDLINE_PARAM, null));

		scotty.setLocalPort(line.getOptionValue(LOCALPORT_CMDLINE_PARAM,
				defaultLocalPort));

		if (line.hasOption(DONT_USE_GATEWAY)) {
			scotty.setUseGateway(false);
		}

		if (line.hasOption(DISABLE_ENCRYPTION_CMDLINE_PARAM)) {
			scotty.setDisableEncryption(true);
		}

		return line;
	}

	/**
	 * Eventhandler for Systray Icon
	 * 
	 * @param event
	 *            exit event
	 * @param o
	 *            optional param
	 */
	@Override
	public void eventReceived(Events event, Object o) {
		if (event.equals(Events.EXIT)) {
			try {
				scotty.stop();
			} catch (Exception e) {
				log.warn("Exception while stopping:", e);
			} finally {
				System.exit(0);
			}
		}
	}
}