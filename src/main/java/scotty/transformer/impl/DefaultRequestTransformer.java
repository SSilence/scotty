package scotty.transformer.impl;

import java.util.Random;

import org.owasp.webscarab.model.Request;

import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;
import scotty.crypto.RSAEncryption;
import scotty.transformer.RequestTransformer;

public class DefaultRequestTransformer implements RequestTransformer {

	private KeyManager keyManager;

	public DefaultRequestTransformer(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@Override
	public byte[] transformRequest(Request request) {

		return request.toString().getBytes();
		
		/*
		// generate AES key
		String randomAesPassword = generateRandomString(16);

		System.out.println("AES Password: " + randomAesPassword);

		// encrypt AES key with RSA
		byte[] encryptedRandomAesPassword = null;
		try { 
			// ToDo: use gateways public key not clients
			encryptedRandomAesPassword = RSAEncryption.encrypt(
					randomAesPassword.getBytes(), keyManager.getPublicKey());
		} catch (CryptoException e) {
			e.printStackTrace();
		}

		return encryptedRandomAesPassword;

		// byte[] plainRequest = request.toString().getBytes();
		*/
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
}
