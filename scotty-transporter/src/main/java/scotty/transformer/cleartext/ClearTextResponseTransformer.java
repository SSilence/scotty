package scotty.transformer.cleartext;

import java.io.ByteArrayInputStream;

import org.owasp.webscarab.model.Response;

import scotty.transformer.ResponseTransformer;

/**
 * This {@link ResponseTransformer} does no encryption, it just returns the
 * response.
 * 
 * @author flo
 * 
 */
public class ClearTextResponseTransformer implements ResponseTransformer {

	/**
	 * No decryption done here, just parses the response into a {@link Response}
	 * .
	 */
	@Override
	public Response transformResponse(byte[] response) {
		Response r = new Response();

		try {
			r.read(new ByteArrayInputStream(response));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

}
