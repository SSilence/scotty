package scotty.transformer.impl;

import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.owasp.webscarab.model.Request;

import com.sun.tools.javac.code.Attribute.Array;

import scotty.crypto.AESEncryption;
import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;
import scotty.crypto.RSAEncryption;
import scotty.transformer.RequestTransformer;

import sun.misc.BASE64Encoder;

public class DefaultRequestTransformer implements RequestTransformer {

	private KeyManager keyManager;
	
	private boolean disableEncryption;

	public DefaultRequestTransformer(KeyManager keyManager, boolean disableEncryption) {
		this.keyManager = keyManager;
		this.disableEncryption = disableEncryption;
	}

	@Override
	public byte[] transformRequest(Request request) {
		// is encryption disabled?
		if(disableEncryption) {
			return request.toString().getBytes();
		}
		
		// content as byte array
		byte[] plainRequest = request.toString().getBytes();

		// generate AES key
		String randomAesPassword = generateRandomString(16);

		// encrypt AES key with RSA
		byte[] encryptedRandomAesPassword = null;
		try {
			// ToDo: use gateways public key not clients
			encryptedRandomAesPassword = RSAEncryption.encrypt(
					randomAesPassword.getBytes(), keyManager.getGatewaysPublicKey());
		} catch (CryptoException e) {
			e.printStackTrace();
		}

		// encrypt request with AES
		byte[] encryptedRequest = null;
		try {
			encryptedRequest = AESEncryption.encrypt(plainRequest,
					randomAesPassword);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// merge key, sign and content
		byte[] encryptedRequestWithSignAndKey = merge(encryptedRandomAesPassword, encryptedRequest);
		
		// base64 encode
		byte[] encryptedRequestWithSignAndKeyBase64 = new Base64().encode(encryptedRequestWithSignAndKey);
		
		return encryptedRequestWithSignAndKeyBase64;
	}

	/**
	 * Generates an random string
	 * 
	 * @param length
	 *            of the string
	 * @return random string
	 */
	private static String generateRandomString(int length) {
		String allowedChars = "0123456789abcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		int max = allowedChars.length();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int value = random.nextInt(max);
			buffer.append(allowedChars.charAt(value));
		}
		return buffer.toString();
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
