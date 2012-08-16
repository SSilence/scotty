package scotty.util;

/**
 * This class provides UserAgents strings.
 * 
 * @author flo
 * 
 */
public class UserAgentProvider {

	private String[] uas = new String[] { "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1"

	};

	public String getUserAgent() {
		return uas[0];
	}
}
