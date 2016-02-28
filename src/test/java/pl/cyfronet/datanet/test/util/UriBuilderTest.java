package pl.cyfronet.datanet.test.util;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

public class UriBuilderTest {
	@Test
	public void shouldComposeValidUrl() {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://host.pl/");
		builder.path("/path");
		builder.path("/path2");
		Assert.assertEquals("http://host.pl/path/path2", builder.build().toUriString());
	}
}