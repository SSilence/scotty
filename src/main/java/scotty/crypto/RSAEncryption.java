package scotty.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Encrypt, Decrypt with RSA.
 * 
 * @author Tobias Zeising tobias.zeising@aditu.de http://www.aditu.de
 */
public class RSAEncryption {

	/**
	 * Encrypt content with given key using RSA
	 * 
	 * @param content
	 * @param key
	 * @return
	 * @throws CryptoException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encrypt(byte[] content, Key key)
			throws CryptoException {
		return encryptDecrypt(content, key, Cipher.ENCRYPT_MODE);
	}

	/**
	 * Decrypt content with given key using RSA
	 * 
	 * @param content
	 * @param key
	 * @return
	 * @throws CryptoException
	 */
	public static byte[] decrypt(byte[] content, Key key)
			throws CryptoException {
		return encryptDecrypt(content, key, Cipher.DECRYPT_MODE);
	}

	/**
	 * Encrypt or decrypt with AES
	 * 
	 * @param content
	 * @param key
	 * @param decrypt
	 * @return
	 * @throws CryptoException
	 */
	private static byte[] encryptDecrypt(byte[] content, Key key, int mode)
			throws CryptoException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(mode, key);
			return cipher.doFinal(content);
		} catch (Exception e) {
			throw new CryptoException("error decrypting with RSA: "
					+ e.getMessage());
		}
	}

	/**
	 * Sign content with private key (hashed with SHA-256).
	 * 
	 * @param content
	 * @param key
	 * @return
	 * @throws CryptoException
	 * @throws
	 */
	public static byte[] sign(byte[] content, PrivateKey privateKey)
			throws CryptoException {

		// hash content
		byte[] hashed = sha256(content);

		// encrypt hash with private key
		return encrypt(hashed, privateKey);

	}

	/**
	 * Check given sign
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 * @throws CryptoException
	 */
	public static boolean verifySign(byte[] content, byte[] sign,
			PublicKey publicKey) throws CryptoException {
		// hash content
		byte[] hashed = sha256(content);

		// decrypt sign
		byte[] decryptedSign = decrypt(sign, publicKey);

		return Arrays.equals(hashed, decryptedSign);
	}

	/**
	 * Hashes a byte array with SHA256
	 * 
	 * @param content
	 * @return
	 * @throws CryptoException
	 */
	private static byte[] sha256(byte[] content) throws CryptoException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(content);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException("error hashing no such algorithm: "
					+ e.getMessage());
		}
	}

}
