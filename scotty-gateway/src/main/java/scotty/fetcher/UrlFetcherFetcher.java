package scotty.fetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.owasp.webscarab.httpclient.URLFetcher;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;

/**
 * Fetcher Impl, that uses the {@link URLFetcher} of WebScarab.
 *
 * @author flo
 *
 */
public class UrlFetcherFetcher implements Fetcher {

	@Override
	public byte[] fetch(byte[] request) {
		Request r = new Request();
		byte[] responseBytes = null;
		try {
			r.read(new ByteArrayInputStream(request));
			URLFetcher urlFetcher = new URLFetcher();

			Response response = urlFetcher.fetchResponse(r);
			response.setRawContent(true);
			responseBytes = createResponse(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseBytes;
	}

	private byte[] createResponse(Response response) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String code = response.getStatus();
		byte[] content = new byte[0];

		String[] heads = response.getHeaderNames();

		content = response.getContent();

		try {
			bos.write("HTTP/1.1 ".getBytes());

			bos.write(String.valueOf(code).getBytes());
			bos.write(" ".getBytes());
			bos.write(getHttpReply(new Integer(code)).getBytes());
			bos.write("\r\n".getBytes());

			for (String headName : heads) {
				String responseHeader = response.getHeader(headName);
				bos.write(headName.getBytes());
				bos.write(":".getBytes());
				bos.write(responseHeader.getBytes());
				bos.write("\r\n".getBytes());
			}

			bos.write("\r\n".getBytes());
			if (content != null) {
				bos.write(content);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	public static String getHttpReply(int codevalue) {
		String key, ret;
		int i;

		ret = null;
		key = "" + codevalue;
		for (i = 0; i < HttpReplies.length; i++) {
			if (HttpReplies[i][0].equals(key)) {
				ret = codevalue + " " + HttpReplies[i][1];
				break;
			}
		}

		return ret;
	}

}
