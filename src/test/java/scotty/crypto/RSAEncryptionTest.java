package scotty.crypto;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

import java.util.Arrays;

import org.easymock.classextension.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
		KeyManager keyManager = new KeyManager();
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
		KeyManager keyManager = new KeyManager();
		keyManager.generateKeyPair();

		byte[] content = new byte[] { 'a', 'b', 'c' };
		byte[] sign = RSAEncryption.sign(content, keyManager.getPrivateKey());
		assertFalse(Arrays.equals(content, sign));
		assertTrue(RSAEncryption.verifySign(content, sign,
				keyManager.getPublicKey()));
	}
}