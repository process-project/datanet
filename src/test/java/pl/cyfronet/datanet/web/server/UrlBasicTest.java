package pl.cyfronet.datanet.web.server;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class UrlBasicTest {
	@Test
	public void urlTest() throws MalformedURLException {
		Assert.assertEquals("", new URL("https://datanet.plgrid.pl").getPath());
		Assert.assertEquals("/", new URL("https://datanet.plgrid.pl/").getPath());
		Assert.assertEquals("/datanet", new URL("https://datanet.plgrid.pl/datanet").getPath());
		
		Assert.assertEquals("datanet.plgrid.pl", new URL("https://datanet.plgrid.pl").getAuthority());
		Assert.assertEquals("datanet.plgrid.pl", new URL("https://datanet.plgrid.pl/").getAuthority());
	}
}