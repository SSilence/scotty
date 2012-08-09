package scotty.crypto;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt and decrypt with AES.
 * 
 * @author Tobias Zeising tobias.zeising@aditu.de http://www.aditu.de
 */
public class AESEncryption {

	/**
	 * Encrypt given content with AES.
	 * 
	 * @param content
	 * @param password
	 * @return encrypted content
	 * @throws CryptoException
	 */
	public static byte[] encrypt(byte[] content, String password)
			throws CryptoException {
		return deAndEncrypt(content, password, false);
	}

	/**
	 * Decrypt given content with AES.
	 * 
	 * @param content
	 * @param password
	 * @return plain text
	 * @throws CryptoException
	 */
	public static byte[] decrypt(byte[] content, String password)
			throws CryptoException {
		return deAndEncrypt(content, password, true);
	}

	/**
	 * Encrypt or decrypt content with password.
	 * 
	 * @param content
	 * @param password
	 * @param decrypt
	 * @return decrypted or encrypted content
	 * @throws CryptoException
	 */
	private static byte[] deAndEncrypt(byte[] content, String password,
			boolean decrypt) throws CryptoException {
		try {
			// hash password for correct password length of 128 bit
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte[] hashedPassword = md.digest();
			byte[] shortenHashedPassword = Arrays.copyOf(hashedPassword, 16);

			// encrypt/decrypt
			SecretKeySpec key = new SecretKeySpec(shortenHashedPassword, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE,
					key);
			return cipher.doFinal(content);
		} catch (Exception e) {
			throw new CryptoException("Error AES encrypt: " + e.getMessage());
		}
	}

}
