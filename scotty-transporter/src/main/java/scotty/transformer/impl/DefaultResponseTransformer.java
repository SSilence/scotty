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

	private boolean disableEncryption;

	public DefaultResponseTransformer(KeyManager keyManager,
			boolean disableEncryption) {
		this.keyManager = keyManager;
		this.disableEncryption = disableEncryption;
	}

	@Override
	public Response transformResponse(byte[] response) {

		if (disableEncryption == false) {
			try {
				Base64 base64 = new Base64();
				response = base64.decode(response);
				response = AESEncryption.decrypt(response, keyManager.getCurrentAESPassword());
			} catch (CryptoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
