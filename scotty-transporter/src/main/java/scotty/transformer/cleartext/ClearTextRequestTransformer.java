package scotty.transformer.cleartext;

import org.owasp.webscarab.model.Request;

import scotty.transformer.RequestTransformer;

/**
 * This {@link RequestTransformer} does no encryption and just returns the same
 * request.
 * 
 * @author flo
 * 
 */
public class ClearTextRequestTransformer implements RequestTransformer {

	/**
	 * Does no transformation, returns the original request.
	 */
	@Override
	public byte[] transformRequest(Request request) {
		return request.toString().getBytes();
	}
}
