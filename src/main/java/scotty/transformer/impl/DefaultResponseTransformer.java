package scotty.transformer.impl;

import java.io.ByteArrayInputStream;

import org.owasp.webscarab.model.Response;

import scotty.crypto.KeyManager;
import scotty.transformer.ResponseTransformer;



public class DefaultResponseTransformer implements ResponseTransformer {

	private KeyManager keyManager;
	
	private boolean disableEncryption;
	
	public DefaultResponseTransformer(KeyManager keyManager, boolean disableEncryption) {
		this.keyManager = keyManager;
		this.disableEncryption = disableEncryption;
	}
	
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
