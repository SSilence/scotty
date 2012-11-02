package scotty;

import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;
import scotty.fetcher.UrlFetcherFetcher;

public class StandaloneGatewayMain {

	private static Logger log = Logger.getLogger(GatewayServlet.class.getName());
			
	/**
	 * CLI to specify the local port
	 */
	private static final String LOCALPORT_CMDLINE_PARAM = "p";
	
	/**
	 * CLI for creating a new key pair
	 */
	private static final String TOKENTIMEOUT_CMDLINE_PARAM = "timeout";
	
	/**
	 * CLI for public key
	 */
	private static final String PUBLICKEY_CMDLINE_PARAM = "publickey";
	
	/**
	 * CLI for private key
	 */
	private static final String PRIVATEKEY_CMDLINE_PARAM = "privatekey";
	
	/**
	 * CLI password for private key
	 */
	private static final String PRIVATEKEYPASS_CMDLINE_PARAM = "privatekeypassword";

	/**
	 * CLI for public key
	 */
	private static final String CLIENTSPUBLICKEY_CMDLINE_PARAM = "clientspublickeys";
	
	/**
	 * default private key
	 */
	private static final int DEFAULT_PORT = 9000;

	
	public static void main(String[] args) throws Exception {
		GatewayServlet gatewayServlet = new GatewayServlet(false);
		gatewayServlet.setFetcher(new UrlFetcherFetcher());
		
		// parse command line params
		CommandLine commandLine = handleCommandline(args);
		loadKeys(commandLine, gatewayServlet.getKm());
		
		int port = DEFAULT_PORT;
		if (commandLine.hasOption(LOCALPORT_CMDLINE_PARAM)) {
			port = Integer.parseInt(commandLine.getOptionValue(LOCALPORT_CMDLINE_PARAM));
			log.info("Use given port "+ commandLine.getOptionValue(LOCALPORT_CMDLINE_PARAM));
		}
		
		// start server
		Server server = new Server(port);
		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(gatewayServlet), "/*");
		server.start();
	}

	
	private static CommandLine handleCommandline(String[] args) throws Exception {
		Options opts = new Options();
		
		opts.addOption(PRIVATEKEY_CMDLINE_PARAM, true, "private key");
		opts.addOption(PUBLICKEY_CMDLINE_PARAM, true, "public key");
		opts.addOption(OptionBuilder.hasOptionalArg().withDescription("private key password (if one was set)").create(PRIVATEKEYPASS_CMDLINE_PARAM));
		opts.addOption(CLIENTSPUBLICKEY_CMDLINE_PARAM, true, "public keys of clients");
		opts.addOption(TOKENTIMEOUT_CMDLINE_PARAM, true, "timeout for the RSA token (default = 1.5 hours)");
		opts.addOption(LOCALPORT_CMDLINE_PARAM, true, "Local port, where gateway listens for requests");
		
		CommandLineParser cmd = new PosixParser();
		CommandLine line = cmd.parse(opts, args);
		return line;
	}
	
	
	/**
	 * Load Keys for RSA Encryption.
	 * 
	 * @param commandLine
	 * @throws CryptoException
	 */
	private static void loadKeys(CommandLine commandLine, KeyManager keyManager) throws CryptoException {	
		// read private key
		if (commandLine.hasOption(PRIVATEKEY_CMDLINE_PARAM)) {
			String password = commandLine.hasOption(PRIVATEKEYPASS_CMDLINE_PARAM) ? commandLine.getOptionValue(PRIVATEKEYPASS_CMDLINE_PARAM) : "";
			if(password==null) {
				System.out.print("password for private key: ");
				password = new String(System.console().readPassword());
			}
			keyManager.readPrivateKey(commandLine.getOptionValue(PRIVATEKEY_CMDLINE_PARAM), password);
			log.info("Use given private key "+ commandLine.getOptionValue(PRIVATEKEY_CMDLINE_PARAM));
		}

		// read public key
		if (commandLine.hasOption(PUBLICKEY_CMDLINE_PARAM)) {
			keyManager.readPublicKey(commandLine.getOptionValue(PUBLICKEY_CMDLINE_PARAM));
			log.info("Use given public key "+ commandLine.getOptionValue(PUBLICKEY_CMDLINE_PARAM));
		}
		
		// read public keys of clients
		if (commandLine.hasOption(CLIENTSPUBLICKEY_CMDLINE_PARAM)) {
			keyManager.readClientPublicKey(commandLine.getOptionValue(CLIENTSPUBLICKEY_CMDLINE_PARAM));
			log.info("Use given clients public key list "+ commandLine.getOptionValue(CLIENTSPUBLICKEY_CMDLINE_PARAM));
		}
		
		// set token timeout if given
		if (commandLine.hasOption(TOKENTIMEOUT_CMDLINE_PARAM)) {
			long tokenTimeout = Long.parseLong(commandLine.getOptionValue(TOKENTIMEOUT_CMDLINE_PARAM));
			keyManager.setMaxValidityOfToken(tokenTimeout);
			log.info("Use given token timeout "+ commandLine.getOptionValue(TOKENTIMEOUT_CMDLINE_PARAM));
		}

	}
}
