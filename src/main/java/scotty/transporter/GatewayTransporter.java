package scotty.transporter;

import scotty.model.Request;
import scotty.model.Response;
import scotty.transporter.transformer.RequestTransformer;

/**
 * The GatewayTransporter takes a {@link Request}, transformes it with a
 * {@link RequestTransformer} into a gateway-compliant format, and communicates
 * with the gateway. The gateway sends a response to the GatewayTransporter, the
 * response is converted to a {@link Response} object, known to scotty.
 * 
 * @author flo
 * 
 */
public interface GatewayTransporter {

	/**
	 * Sends the request, receives the response.
	 * 
	 * @param request
	 *            request.
	 * @return response.
	 */
	public Response sendAndReceive(Request request) throws Exception; // FIXME
																		// Exception
																		// handling.
}
