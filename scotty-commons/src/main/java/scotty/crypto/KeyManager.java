package scotty.crypto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;



/**
 * Create, load and save keys.
 * 
 * @author Tobias Zeising tobias.zeising@aditu.de http://www.aditu.de
 */
public class KeyManager {

	/**
	 * Constant for no token set
	 */
	public static final int NO_TOKEN_SET = -1;

	/**
	 * Length of the RSA Key
	 */
	public static final int KEY_LENGTH = 2048;

	/**
	 * Private Key
	 */
	private PrivateKey privateKey = null;

	/**
	 * Public Key
	 */
	private PublicKey publicKey = null;

	/**
	 * Public Key of the gateway
	 */
	private PublicKey gatewaysPublicKey = null;

	/**
	 * Current RSA signed AES Password
	 */
	private byte[] currentToken;

	/**
	 * Current AES Password
	 */
	private String currentAESPassword;

	/**
	 * Timestamp where current token was created
	 */
	private long currentTokenTimestamp = NO_TOKEN_SET;

	/**
	 * Max validity of token
	 */
	private long maxValidityOfToken;

	/**
	 * List of allowed client public keys.
	 */
	private List<PublicKey> clientPublicKeys = new ArrayList<PublicKey>();


	/**
	 * Generate new key pair.
	 * 
	 * @throws CryptoException
	 *             any error
	 */
	public void generateKeyPair() throws CryptoException {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(KEY_LENGTH);
			KeyPair keyPair = kpg.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException("Error, can't create keypair: " + e.getMessage());
		}
	}



	/**
	 * Write private key to file (private key will be encrypted with AES first)
	 * 
	 * @param filename
	 *            private key file
	 * @param password
	 *            password for AES encryption
	 * @throws CryptoException
	 *             Exception on AES encryption or IO
	 */
	public void writePrivateKey(String filename, String password) throws CryptoException {
		if (privateKey == null) {
			throw new CryptoException("no private key set");
		}

		byte[] privateKeyAsByteArray = privateKey.getEncoded();
		if (password != null && password.length() != 0)
			privateKeyAsByteArray = AESEncryption.encrypt(privateKey.getEncoded(), password);

		writeKey(filename, privateKeyAsByteArray);
	}



	/**
	 * Write public key to file.
	 * 
	 * @param filename
	 *            public key file
	 * @throws CryptoException
	 *             IO exception
	 */
	public void writePublicKey(String filename) throws CryptoException {
		if (publicKey == null) {
			throw new CryptoException("no public key set");
		}
		writeKey(filename, publicKey.getEncoded());
	}



	/**
	 * Writes back given byte array into a given file.
	 * 
	 * @param filename
	 * @param key
	 * @throws CryptoException
	 */
	private void writeKey(String filename, byte[] key) throws CryptoException {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			out.write(new Base64().encode(key));
			out.close();
		} catch (IOException e) {
			throw new CryptoException("Error writing public key into file: " + e.getMessage());
		}
	}



	/**
	 * Reads a private key from file, decrypts it with password (AES) and saves the key in KeyManager.
	 * 
	 * @param filename
	 * @param password
	 * @throws CryptoException
	 */
	public void readPrivateKey(String filename, String password) throws CryptoException {
		try {
			byte[] privateKeyFromFile = readFileBase64Decoded(filename);
			if (password != null && password.length() != 0)
				privateKeyFromFile = AESEncryption.decrypt(privateKeyFromFile, password);
			this.privateKey = parsePrivateKeyFromByteArray(privateKeyFromFile);
		} catch (Exception e) {
			throw new CryptoException("Error, can't read private key: " + e.getMessage());
		}
	}



	/**
	 * Read public key and saves it in KeyManager.
	 * 
	 * @param filename
	 * @throws CryptoException
	 */
	public void readPublicKey(String filename) throws CryptoException {
		try {
			byte[] publicKey = readFileBase64Decoded(filename);
			this.publicKey = parsePublicKeyFromByteArray(publicKey);
		} catch (Exception e) {
			throw new CryptoException("Error, can't read public key: " + e.getMessage());
		}
	}



	/**
	 * Read gateways public key and saves it in KeyManager.
	 * 
	 * @param filename
	 * @throws CryptoException
	 */
	public void readGatewaysPublicKey(String filename) throws CryptoException {
		try {
			byte[] publicKey = readFileBase64Decoded(filename);
			this.gatewaysPublicKey = parsePublicKeyFromByteArray(publicKey);
		} catch (Exception e) {
			throw new CryptoException("Error, can't read gateways public key: " + e.getMessage());
		}
	}



	/**
	 * Reads the public keys of the clients. One key per line. (Content is Base64 coded).
	 * 
	 * @param filename
	 * @throws CryptoException
	 */
	public void readClientPublicKey(String filename) throws CryptoException {
		try {
			byte[] content = readFile(filename);

			BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));

			String clientPublicKey;
			while ((clientPublicKey = r.readLine()) != null) {
				PublicKey key = parsePublicKeyFromByteArray(Base64.decodeBase64(clientPublicKey));
				clientPublicKeys.add(key);
			}

		} catch (Exception e) {
			throw new CryptoException("Error, can't read gateways public key: " + e.getMessage());
		}
	}



	/**
	 * Reads a given file and return it as byte array.
	 * 
	 * @param filename
	 * @return byte array
	 * @throws CryptoException
	 */
	private byte[] readFile(String filename) throws CryptoException {
		try {

			InputStream fileInputStream = null;

			if (filename.startsWith("resources")) {
				ClassLoader classLoader = getClass().getClassLoader();
				fileInputStream = classLoader.getResourceAsStream(filename.substring(filename.indexOf(":") + 1));
			} else {
				fileInputStream = new FileInputStream(new File(filename));
			}

			byte[] b = inputStreamToByteArray(fileInputStream);
			fileInputStream.close();

			return b;
		} catch (IOException e) {
			throw new CryptoException("Error writing public key into file: " + e.getMessage());
		}
	}



	/**
	 * Reads a given file and return it as byte array, decodes the Base64 content.
	 * 
	 * @param filename
	 * @return byte array
	 * @throws CryptoException
	 */
	private byte[] readFileBase64Decoded(String filename) throws CryptoException {
		byte[] file = readFile(filename);

		return Base64.decodeBase64(file);
	}



	/**
	 * Converts a public key from byte array to PublicKey object
	 * 
	 * @param publicKeyRaw
	 * @return PublicKey
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	private synchronized PublicKey parsePublicKeyFromByteArray(byte[] publicKeyRaw) throws InvalidKeySpecException,
		NoSuchAlgorithmException {
		PublicKey publicKey = null;
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyRaw);
		publicKey = keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}



	/**
	 * Converts a private key from byte array to PrivateKey object
	 * 
	 * @param privateKeyRaw
	 * @return PrivateKey
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	private synchronized PrivateKey parsePrivateKeyFromByteArray(byte[] privateKeyRaw) throws InvalidKeySpecException,
		NoSuchAlgorithmException {
		PrivateKey privateKey = null;
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyRaw);
		privateKey = keyFactory.generatePrivate(privateKeySpec);
		return privateKey;
	}



	/**
	 * Reads from InputStream and return content as byte array
	 * 
	 * @param is
	 *            InputStream
	 * @return byte array
	 * @throws IOException
	 */
	private byte[] inputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}



	/**
	 * Returns current token. If token is no longer valid, a new one will be generated.
	 * 
	 * @return current token
	 * @throws CryptoException
	 */
	public synchronized byte[] getCurrentToken() throws CryptoException {
		try {
			// valid token available?
			long timestamp = (new Date().getTime()) / 1000;
			if (currentTokenTimestamp != NO_TOKEN_SET && timestamp < currentTokenTimestamp + maxValidityOfToken) {
				return currentToken;
			}

			// generate new token
			StringBuilder aesPasswordAndTimestamp = new StringBuilder();

			// generate AES key
			String randomAesPassword = generateRandomString(16);
			aesPasswordAndTimestamp.append(randomAesPassword);
			aesPasswordAndTimestamp.append(new String("|")); // separator
			aesPasswordAndTimestamp.append(timestamp);

			// encrypt token with gateways public key
			byte[] encryptedToken = RSAEncryption.encrypt(aesPasswordAndTimestamp.toString().getBytes(),
					getGatewaysPublicKey());

			// sign AES key and password
			byte[] sign = RSAEncryption.sign(aesPasswordAndTimestamp.toString().getBytes(), privateKey);

			Base64 base64 = new Base64();
			StringBuilder encryptedAesTimestampAndSign = new StringBuilder();
			encryptedAesTimestampAndSign.append(new String(base64.encode(encryptedToken)));
			encryptedAesTimestampAndSign.append(new String("|")); // separator
			encryptedAesTimestampAndSign.append(new String(base64.encode(sign)));

			this.currentToken = encryptedAesTimestampAndSign.toString().getBytes();
			this.currentAESPassword = randomAesPassword;
			this.currentTokenTimestamp = timestamp;

			return currentToken;
		} catch (Exception e) {
			throw new CryptoException(e);
		}

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



	public PrivateKey getPrivateKey() {
		return privateKey;
	}



	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}



	public PublicKey getPublicKey() {
		return publicKey;
	}



	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}



	public PublicKey getGatewaysPublicKey() {
		return gatewaysPublicKey;
	}



	public void setGatewaysPublicKey(PublicKey gatewaysPublicKey) {
		this.gatewaysPublicKey = gatewaysPublicKey;
	}



	public String getCurrentAESPassword() {
		return currentAESPassword;
	}



	public void setCurrentAESPassword(String currentAESPassword) {
		this.currentAESPassword = currentAESPassword;
	}



	public long getCurrentTokenTimestamp() {
		return currentTokenTimestamp;
	}



	public void setCurrentTokenTimestamp(long currentTokenTimestamp) {
		this.currentTokenTimestamp = currentTokenTimestamp;
	}



	public long getMaxValidityOfToken() {
		return maxValidityOfToken;
	}



	public void setMaxValidityOfToken(long maxValidityOfToken) {
		this.maxValidityOfToken = maxValidityOfToken;
	}



	public void setCurrentToken(byte[] currentToken) {
		this.currentToken = currentToken;
	}



	public List<PublicKey> getClientPublicKeys() {
		return clientPublicKeys;
	}



	public void setClientPublicKeys(List<PublicKey> clientPublicKeys) {
		this.clientPublicKeys = clientPublicKeys;
	}

}
