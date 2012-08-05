import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Scotty main command line class.
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class Scotty {
	public static Properties properties;

	public static void main(String[] args) throws Exception {

		// parse command line parameters
		Options options = getOptions();
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		// load default properties
		properties = loadDefaultProperties();

		// load optional config file
		try {
			String filename = "";
			if (cmd.hasOption("config")) {
				filename = cmd.getOptionValue("config");
				Properties configFileProperties = loadProperties(filename);
				properties.putAll(configFileProperties);
			}
		} catch (IOException e) {
			System.err.println("can't load given config file: " +  e.getMessage());
		}

		// start as proxy
		if (cmd.hasOption("proxy")) {
			int port = Integer.parseInt(properties.getProperty("clientProxyPort"));
			ClientServer clientServer = new ClientServer(port);
			clientServer.startClientServer();
		
		// start as gateway
		} else if (cmd.hasOption("gateway")) {
			int port = Integer.parseInt(properties.getProperty("gatewayPort"));
			GatewayServer gatewayServer = new GatewayServer(port);
			gatewayServer.startGatewayServer();
			
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("scotty", options);
		}
	}

	/**
	 * Create command line options.
	 * 
	 * @return command line options
	 */
	private static Options getOptions() {
		Options options = new Options();
		options.addOption("proxy", false, "start as client proxy");
		options.addOption("gateway", false, "start as gateway server");

		Option configfile = OptionBuilder.withArgName("config").hasArg()
				.withDescription("configfile path and name").create("config");
		options.addOption(configfile);

		return options;
	}

	/**
	 * Load default properties.
	 * 
	 * @return default properties
	 * @throws IOException
	 */
	private static Properties loadDefaultProperties() throws IOException {
		return loadProperties(null);
	}

	/**
	 * Load properties.
	 * 
	 * @return default properties
	 * @throws IOException
	 */
	private static Properties loadProperties(String filename)
			throws IOException {
		InputStream is = null;
		if (filename == null || filename.equals("")) {
			is = ClassLoader.getSystemClassLoader().getResourceAsStream(
					"config.properties");
		} else {
			is = new FileInputStream(filename);
		}
		Properties props = new Properties();
		props.load(is);
		is.close();
		return props;
	}
}