package scotty.fetcher;

import java.io.ByteArrayInputStream;
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



public class GoogleFetcher implements Fetcher {
	private static final String[][] HttpReplies = { { "100", "Continue" }, { "101", "Switching Protocols" },
			{ "200", "OK" }, { "201", "Created" }, { "202", "Accepted" }, { "203", "Non-Authoritative Information" },
			{ "204", "No Content" }, { "205", "Reset Content" }, { "206", "Partial Content" },
			{ "300", "Multiple Choices" }, { "301", "Moved Permanently" }, { "302", "Found" }, { "303", "See Other" },
			{ "304", "Not Modified" }, { "305", "Use Proxy" }, { "306", "(Unused)" }, { "307", "Temporary Redirect" },
			{ "400", "Bad Request" }, { "401", "Unauthorized" }, { "402", "Payment Required" }, { "403", "Forbidden" },
			{ "404", "Not Found" }, { "405", "Method Not Allowed" }, { "406", "Not Acceptable" },
			{ "407", "Proxy Authentication Required" }, { "408", "Request Timeout" }, { "409", "Conflict" },
			{ "410", "Gone" }, { "411", "Length Required" }, { "412", "Precondition Failed" },
			{ "413", "Request Entity Too Large" }, { "414", "Request-URI Too Long" },
			{ "415", "Unsupported Media Type" }, { "416", "Requested Range Not Satisfiable" },
			{ "417", "Expectation Failed" }, { "500", "Internal Server Error" }, { "501", "Not Implemented" },
			{ "502", "Bad Gateway" }, { "503", "Service Unavailable" }, { "504", "Gateway Timeout" },
			{ "505", "HTTP Version Not Supported" } };

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
			if (!(resource.indexOf("http:") == 0) && !(resource.indexOf("https:") == 0)) {
				for (Header header : h) {
					if ("Host".equalsIgnoreCase(header.getName())) {
						url = "http://" + header.getValue() + "/" + url;
					}
				}
			}

			URL reqUrl = new URL(url);
			HTTPRequest req = new HTTPRequest(reqUrl, HTTPMethod.valueOf(method.toUpperCase()));
			for (Header header : h) {
				HTTPHeader head = new HTTPHeader(header.getName(), header.getValue());
				req.addHeader(head);
			}

			HTTPResponse resp = fetchService.fetch(req);

			String responseMsg = createResponse(resp);
			response = responseMsg.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}



	private String createResponse(HTTPResponse response) {
		StringBuilder b = new StringBuilder();

		int code = response.getResponseCode();
		byte[] content = response.getContent();
		List<HTTPHeader> heads = response.getHeaders();

		b.append("HTTP/1.1 ");
		b.append(code);
		b.append(" ");
		b.append(getHttpReply(code));
		b.append("\r\n");

		for (HTTPHeader responseHeader : heads) {
			b.append(responseHeader.getName());
			b.append(":");
			b.append(responseHeader.getValue());
			b.append("\r\n");
		}
		b.append("\r\n");
		if (content != null) {
			b.append(content);
		}

		return b.toString();
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
