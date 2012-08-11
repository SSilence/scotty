package scotty.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.codec.binary.Base64;

/**
 * Create, load and save keys.
 * 
 * @author Tobias Zeising tobias.zeising@aditu.de http://www.aditu.de
 */
public class KeyManager {

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
			throw new CryptoException("Error, can't create keypair: "
					+ e.getMessage());
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
	public void writePrivateKey(String filename, String password)
			throws CryptoException {
		if (privateKey == null) {
			throw new CryptoException("no private key set");
		}

		byte[] privateKeyAsByteArray = privateKey.getEncoded();
		if (password != null && password.length() != 0)
			privateKeyAsByteArray = AESEncryption.encrypt(
					privateKey.getEncoded(), password);

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
			throw new CryptoException("Error writing public key into file: "
					+ e.getMessage());
		}
	}

	/**
	 * Reads a private key from file, decrypts it with password (AES) and saves
	 * the key in KeyManager.
	 * 
	 * @param filename
	 * @param password
	 * @throws CryptoException
	 */
	public void readPrivateKey(String filename, String password)
			throws CryptoException {
		try {
			byte[] privateKeyFromFile = readFile(filename);
			if (password != null && password.length() != 0)
				privateKeyFromFile = AESEncryption.decrypt(privateKeyFromFile,
						password);
			this.privateKey = parsePrivateKeyFromByteArray(privateKeyFromFile);
		} catch (Exception e) {
			throw new CryptoException("Error, can't read private key: "
					+ e.getMessage());
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
			byte[] publicKey = readFile(filename);
			this.publicKey = parsePublicKeyFromByteArray(publicKey);
		} catch (Exception e) {
			throw new CryptoException("Error, can't read public key: "
					+ e.getMessage());
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
			byte[] publicKey = readFile(filename);
			this.gatewaysPublicKey = parsePublicKeyFromByteArray(publicKey);
		} catch (Exception e) {
			throw new CryptoException("Error, can't read gateways public key: "
					+ e.getMessage());
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
			
			if(filename.startsWith("resources")) {
				ClassLoader classLoader = getClass().getClassLoader();
				fileInputStream = classLoader.getResourceAsStream(filename.substring(filename.indexOf(":")+1));
			} else {
				fileInputStream = new FileInputStream(new File(filename));
			}
			
			byte[] b = inputStreamToByteArray(fileInputStream);
			fileInputStream.close();
			
			return new Base64().decode(b);
		} catch (IOException e) {
			throw new CryptoException("Error writing public key into file: "
					+ e.getMessage());
		}
	}

	/**
	 * Converts a public key from byte array to PublicKey object
	 * 
	 * @param publicKeyRaw
	 * @return PublicKey
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	private synchronized PublicKey parsePublicKeyFromByteArray(
			byte[] publicKeyRaw) throws InvalidKeySpecException,
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
	private synchronized PrivateKey parsePrivateKeyFromByteArray(
			byte[] privateKeyRaw) throws InvalidKeySpecException,
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

}
