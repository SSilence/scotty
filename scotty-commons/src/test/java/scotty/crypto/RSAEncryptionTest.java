package scotty.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * Test RSA Encryption
 * 
 * @author Tobias Zeising tobias.zeising@aditu.de http://www.aditu.de
 */
public class RSAEncryptionTest {

	/**
	 * Setup.
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() {

	}

	/**
	 * Test RSA Encryption
	 * 
	 * @throws CryptoException
	 */
	@Test
	public void testEncryptDecrypt() throws CryptoException {
		KeyManager keyManager = KeyManager.getInstance();
		keyManager.generateKeyPair();

		byte[] content = new byte[] { 'a', 'b', 'c' };
		byte[] encrypted = RSAEncryption.encrypt(content,
				keyManager.getPublicKey());
		byte[] decrypted = RSAEncryption.decrypt(encrypted,
				keyManager.getPrivateKey());

		assertFalse(Arrays.equals(content, encrypted));
		assertArrayEquals(content, decrypted);
	}

	/**
	 * Test signing with RSA
	 * 
	 * @throws CryptoException
	 */
	@Test
	public void testSign() throws CryptoException {
		KeyManager keyManager = KeyManager.getInstance();
		keyManager.generateKeyPair();

		byte[] content = new byte[] { 'a', 'b', 'c' };
		byte[] sign = RSAEncryption.sign(content, keyManager.getPrivateKey());
		assertFalse(Arrays.equals(content, sign));
		assertTrue(RSAEncryption.verifySign(content, sign,
				keyManager.getPublicKey()));
	}
}