package scotty.model;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Request is the original Request that should be executed by the gateway. This
 * Class is used to transer the request from scotty to the gateway.
 * 
 * The gateway uses the information in this object to initiate the real request
 * to a remote Site.
 * 
 * @author flo
 * 
 */
public class Request {

	/**
	 * Indicate https request.
	 */
	private boolean secure;

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
	 * Body of this request (e.g POST data).
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

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
}
