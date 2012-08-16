package scotty;

/**
 * One message of the client to the gateway.
 * 
 * @author flo
 */
public class Message {
	/**
	 * The crypted Token.
	 */
	private String cryptedToken;

	/**
	 * The signature.
	 */
	private byte[] signature;

	/**
	 * The crypted Message.
	 */
	private String encryptedMessage;



	public String getCryptedToken() {
		return cryptedToken;
	}



	public void setCryptedToken(String cryptedToken) {
		this.cryptedToken = cryptedToken;
	}



	public String getEncryptedMessage() {
		return encryptedMessage;
	}



	public void setEncryptedMessage(String encryptedMessage) {
		this.encryptedMessage = encryptedMessage;
	}



	public byte[] getSignature() {
		return signature;
	}



	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
}