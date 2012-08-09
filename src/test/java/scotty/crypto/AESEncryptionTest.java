package scotty.crypto;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

import org.easymock.classextension.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test AES Encryption
 * 
 * @author Tobias Zeising tobias.zeising@aditu.de http://www.aditu.de
 */
public class AESEncryptionTest {

	/**
	 * Setup.
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() {

	}

	/**
	 * Test AES Encryption
	 * 
	 * @throws CryptoException
	 */
	@Test
	public void testEncryptDecryptLotOfData() throws CryptoException {
		String password = "testpassword12345";
		StringBuilder testString = new StringBuilder();
		for (int i = 0; i < 10000; i++)
			testString.append(i);
		byte[] content = testString.toString().getBytes();
		byte[] encrypted = AESEncryption.encrypt(content, password);
		byte[] decrypted = AESEncryption.decrypt(encrypted, password);

		assertArrayEquals(content, decrypted);
	}

}