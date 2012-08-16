package scotty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import scotty.crypto.CryptoException;
import scotty.crypto.KeyManager;



public class GatewayServlet extends HttpServlet {
	
	private KeyManager km = new KeyManager();
	
	public GatewayServlet() {
		try {
			km.readPublicKey("resources:gw-defaultpublickey");
			km.readPrivateKey("resources:gw-defaultprivatekey", null);
			km.readGatewaysPublicKey("");
			
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Message m = getContent(req);
		
		
	}

	public Message getContent(HttpServletRequest req) throws IOException {

		BufferedReader r = new BufferedReader(new InputStreamReader(req.getInputStream()));
		StringBuilder b = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			b.append(line);
		}

		String c = b.toString();
		String[] contents = c.split("|");

		Message m = new Message();
		m.token = contents[0];
		m.signature = contents[1];
		m.encryptedMessage = contents[2];

		return m;
	}
	
	public String getAesPassword(String token) {
		// Cache pruefen.
		
		byte[] cryptedToken = Base64.decodeBase64(token);
		// token mit privkey entschluesseln.
		
		
		
		// token timestamp pruefen
		
		return null;
	}
	
	
	
	
	
	class Token {
		String aesPassword;
		long timestamp;
	}

	class Message {
		String token;
		String signature;
		String encryptedMessage;
	}

}
