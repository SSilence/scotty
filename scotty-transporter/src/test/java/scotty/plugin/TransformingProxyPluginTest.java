package scotty.plugin;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.owasp.webscarab.model.HttpUrl;

public class TransformingProxyPluginTest {

	private TransformingProxyPlugin underTest;

	@Before
	public void setup() {
		underTest = new TransformingProxyPlugin();
	}

	@Test
	public void testCreateHttpsGatewayUrl_Portnumber_as_last_Part_of_Url() throws Exception {
		String url = "http://www.scotty-transporter.org:9001";
		String expected =  "http://www.scotty-transporter.org:9001/?ssl=true";

		HttpUrl actualUrl = underTest.createHttpsGatewayUrl(url);
		String actual = actualUrl.toString();

		Assert.assertEquals(expected, actual);
	}


	@Test
	public void testCreateHttpsGatewayUrl_Slash_Suffix() throws Exception {
		String url = "http://www.scotty-transporter.org:9001/";
		String expected =  "http://www.scotty-transporter.org:9001/?ssl=true";

		HttpUrl actualUrl = underTest.createHttpsGatewayUrl(url);
		String actual = actualUrl.toString();

		Assert.assertEquals(expected, actual);
	}

}
