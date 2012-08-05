package scotty.model;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Response is the original Response that the gateway received. This Class is
 * used to transfer the Response from the gateway to the scotty.
 * 
 * @author flo
 * 
 */
public class Response {
	/**
	 * Response code.
	 */
	private int code;

	/**
	 * HTTP Method.
	 */
	private String method;

	/**
	 * URI.
	 */
	private String uri;

	/**
	 * HTTP Headers.
	 */
	private Set<Entry<String, List<String>>> headers;

	/**
	 * Body of this response (content of a page).
	 */
	private String body;
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Set<Entry<String, List<String>>> getHeaders() {
		return headers;
	}

	public void setHeaders(Set<Entry<String, List<String>>> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
