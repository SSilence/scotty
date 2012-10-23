package scotty.transformer.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.owasp.webscarab.model.Response;

import scotty.crypto.AESEncryption;
import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;
import scotty.transformer.ResponseTransformer;

public class DefaultResponseTransformer implements ResponseTransformer {

	private KeyManager keyManager;

	public DefaultResponseTransformer(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@Override
	public Response transformResponse(byte[] response) {
		try {
			Base64 base64 = new Base64();
			response = base64.decode(response);
			response = AESEncryption.decrypt(response,
					keyManager.getCurrentAESPassword());
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Response r = new Response();
		r.setRawContent(true);
		try {
			r.read(new ByteArrayInputStream(response));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

}
