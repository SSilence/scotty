package scotty.transformer.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.owasp.webscarab.model.Request;

import scotty.crypto.AESEncryption;
import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;
import scotty.transformer.RequestTransformer;

/**
 * Default request transformer, which is encrypting the request.
 * 
 * @author flo
 *
 */
public class DefaultRequestTransformer implements RequestTransformer {
	
	private static Logger log = Logger.getLogger(DefaultRequestTransformer.class);

	private KeyManager keyManager;

	public DefaultRequestTransformer(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@Override
	public byte[] transformRequest(Request request) {
		if ( log.isTraceEnabled()) {
			log.trace("Entering transformRequest: " + request.getURL() );
		}
		try {
			// content as byte array
			byte[] plainRequest = request.toString().getBytes();

			// get current token and aes password
			String aesPassword = "";
			byte[] token = null;
			synchronized (keyManager) {
				token = keyManager.getCurrentToken();
				aesPassword = keyManager.getCurrentAESPassword();
			}

			// encrypt request with AES
			byte[] encryptedRequest = AESEncryption.encrypt(plainRequest,
					aesPassword);

			// base64 encode all
			Base64 base64 = new Base64();
			byte[] separator = new String("|").getBytes();
			byte[] base64EncryptedRequest = base64.encode(encryptedRequest);

			byte[] base64Request = merge(token, separator);
			base64Request = merge(base64Request, base64EncryptedRequest);

			return base64Request;
		} catch (CryptoException e) {
			log.error("Error transforming request: " + request.getURL(), e);
			if ( log.isTraceEnabled()) {
				log.trace("Error transforming request: " + request.getURL() + ", content: " + new String(request.getContent()));
			}
			return new byte[] {};
		}
	}

	/**
	 * merge two array into one
	 * 
	 * @param array1
	 * @param array2
	 * @return merged array
	 */
	public static byte[] merge(byte[] array1, byte[] array2) {
		byte[] merged = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, merged, 0, array1.length);
		System.arraycopy(array2, 0, merged, array1.length, array2.length);
		return merged;
	}
}
