package scotty.fetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * This fetcher implementation useses the Google Appengine
 * {@link URLFetchService} to fetch the contents.
 *
 * @author flo
 *
 */
public class GoogleFetcher implements Fetcher {


	/**
	 * GAE Fetchservice
	 */
	private URLFetchService fetchService;

	public GoogleFetcher() {
		fetchService = URLFetchServiceFactory.getURLFetchService();
	}

	@Override
	public byte[] fetch(byte[] request) {
		byte[] response = null;

		InputStream is = new ByteArrayInputStream(request);
		try {
			String line = HttpParser.readLine(is);
			String[] lineArr = line.split("\\s");
			String method = lineArr[0];
			String resource = lineArr[1];

			is = new ByteArrayInputStream(request);
			Header[] h = HttpParser.parseHeaders(is);

			String url = resource;
			if (!(resource.indexOf("http:") == 0)
					&& !(resource.indexOf("https:") == 0)) {
				for (Header header : h) {
					if ("Host".equalsIgnoreCase(header.getName())) {
						url = "http://" + header.getValue() + "/" + url;
					}
				}
			}

			URL reqUrl = new URL(url);
			HTTPRequest req = new HTTPRequest(reqUrl, HTTPMethod.valueOf(method
					.toUpperCase()));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] oneLine = null;

			while ((oneLine = HttpParser.readRawLine(is)) != null) {
				bos.write(oneLine);
			}
			byte[] body = bos.toByteArray();
			req.setPayload(body);

			for (Header header : h) {
				HTTPHeader head = new HTTPHeader(header.getName(),
						header.getValue());
				req.addHeader(head);
			}

			HTTPResponse resp = fetchService.fetch(req);

			response = createResponse(resp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	private byte[] createResponse(HTTPResponse response) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int code = response.getResponseCode();
		byte[] content = response.getContent();
		List<HTTPHeader> heads = response.getHeaders();

		try {
			bos.write("HTTP/1.1 ".getBytes());

			bos.write(String.valueOf(code).getBytes());
			bos.write(" ".getBytes());
			bos.write(getHttpReply(code).getBytes());
			bos.write("\r\n".getBytes());

			for (HTTPHeader responseHeader : heads) {
				bos.write(responseHeader.getName().getBytes());
				bos.write(":".getBytes());
				bos.write(responseHeader.getValue().getBytes());
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
