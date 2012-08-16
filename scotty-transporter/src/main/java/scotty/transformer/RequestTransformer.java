package scotty.transformer;

import org.owasp.webscarab.model.Request;

/**
 * Transforms a request to a gateway-compliant format.
 * 
 * @author flo
 * 
 */
public interface RequestTransformer {

	/**
	 * Transform the request to a gateway-compliant, a format the gateway can
	 * understand, format.
	 * 
	 * @param request
	 *            request.
	 * @return transformed to a string.
	 */
	public byte[] transformRequest(Request request);

}
