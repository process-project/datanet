package pl.cyfronet.datanet.web.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import pl.cyfronet.datanet.web.server.config.SpringConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = SpringConfiguration.class)
public class UrlEncoderTest {
	@Value("classpath:proxy.pem") Resource proxy;
	
	@Test
	public void testUrlEncoding() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(proxy.getInputStream(), writer);
		String proxyValue = writer.toString();
		assertTrue(proxyValue.contains("\n"));
		String encodedProxy = URLEncoder.encode(proxyValue, "UTF-8");
		assertTrue(!encodedProxy.contains("\n"));
	}
}