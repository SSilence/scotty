package scotty;
import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP Helper class
 * 
 * @author zeising.tobias <br>
 *         copyright (C) 2012, http://www.aditu.de, tobias.zeising@aditu.de
 */
public class HttpUtils {
	/**
	 * Read full http request. Important: Browser works in keep alive mode, we
	 * will stop reading vom InputStream when no more data is available
	 * (otherwise is.read will block).
	 * 
	 * @param is
	 *            InputStream of the socket
	 * @return String with the full http request
	 * @throws IOException
	 *             Anything wrong with io
	 */
	static public String readFromInputStream(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		byte[] buff = new byte[999];
		while (true) {
			int n = is.read(buff);
			if (n < 0)
				break;
			sb.append(new String(buff, 0, n));
			if (is.available() == 0) {
				break;
			}
		}

		return sb.toString();
	}
}
