package scotty.store;

import java.util.Date;

import org.owasp.webscarab.model.ConversationID;
import org.owasp.webscarab.model.Cookie;
import org.owasp.webscarab.model.HttpUrl;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.model.SiteModelStore;
import org.owasp.webscarab.model.StoreException;

/**
 * This store does nothing, really.
 * 
 * @author flo
 * 
 */
public class NopStore implements SiteModelStore {

	@Override
	public int addConversation(ConversationID id, Date when, Request request,
			Response response) {
		return 0;
	}

	@Override
	public void setConversationProperty(ConversationID id, String property,
			String value) {

	}

	@Override
	public boolean addConversationProperty(ConversationID id, String property,
			String value) {

		return false;
	}

	@Override
	public String[] getConversationProperties(ConversationID id, String property) {

		return null;
	}

	@Override
	public int getIndexOfConversation(HttpUrl url, ConversationID id) {

		return 0;
	}

	@Override
	public int getConversationCount(HttpUrl url) {

		return 0;
	}

	@Override
	public ConversationID getConversationAt(HttpUrl url, int index) {

		return null;
	}

	@Override
	public void addUrl(HttpUrl url) {

	}

	@Override
	public boolean isKnownUrl(HttpUrl url) {

		return false;
	}

	@Override
	public void setUrlProperty(HttpUrl url, String property, String value) {

	}

	@Override
	public boolean addUrlProperty(HttpUrl url, String property, String value) {

		return false;
	}

	@Override
	public String[] getUrlProperties(HttpUrl url, String property) {

		return null;
	}

	@Override
	public int getChildCount(HttpUrl url) {

		return 0;
	}

	@Override
	public HttpUrl getChildAt(HttpUrl url, int index) {

		return null;
	}

	@Override
	public int getIndexOf(HttpUrl url) {

		return 0;
	}

	@Override
	public void setRequest(ConversationID id, Request request) {

	}

	@Override
	public Request getRequest(ConversationID id) {

		return null;
	}

	@Override
	public void setResponse(ConversationID id, Response response) {

	}

	@Override
	public Response getResponse(ConversationID id) {

		return null;
	}

	@Override
	public int getCookieCount() {

		return 0;
	}

	@Override
	public int getCookieCount(String key) {

		return 0;
	}

	@Override
	public String getCookieAt(int index) {

		return null;
	}

	@Override
	public Cookie getCookieAt(String key, int index) {

		return null;
	}

	@Override
	public Cookie getCurrentCookie(String key) {

		return null;
	}

	@Override
	public int getIndexOfCookie(Cookie cookie) {

		return 0;
	}

	@Override
	public int getIndexOfCookie(String key, Cookie cookie) {

		return 0;
	}

	@Override
	public boolean addCookie(Cookie cookie) {

		return false;
	}

	@Override
	public boolean removeCookie(Cookie cookie) {

		return false;
	}

	@Override
	public void flush() throws StoreException {

	}

}
