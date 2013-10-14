package pl.cyfronet.datanet.web.server;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.html.HtmlResolver;
import org.openid4java.discovery.yadis.YadisResolver;
import org.openid4java.server.RealmVerifierFactory;
import org.openid4java.util.HttpFetcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIdDiscoveryTest {
	private static final Logger log = LoggerFactory.getLogger(OpenIdDiscoveryTest.class);
	
	private ConsumerManager consumerManager;
	
	@Before
	public void prepare() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] tma = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, tma, new java.security.SecureRandom());

		HttpFetcherFactory hff = new HttpFetcherFactory(sc);
		YadisResolver yr = new YadisResolver(hff);
		RealmVerifierFactory rvf = new RealmVerifierFactory(yr);
		Discovery d = new Discovery(new HtmlResolver(hff), yr,
				Discovery.getXriResolver());
		consumerManager = new ConsumerManager(rvf, d, hff);
		consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
		consumerManager.setNonceVerifier(new InMemoryNonceVerifier(5000));
	}
	
	@Test
	public void shouldRetrieveOpenIdInfo() throws DiscoveryException {
		String returnToUrl = "http://example.com/openid";
		@SuppressWarnings("unchecked")
		List<DiscoveryInformation> discoveries = consumerManager.discover("https://openid-test.grid.cyf-kr.edu.pl/plglogin");
		log.info("Discoveries: {}", discoveries);
		Assert.assertNotNull(discoveries);
		
		DiscoveryInformation discovered = consumerManager.associate(discoveries);
		log.info("Discovered: {}", discovered);
		Assert.assertNotNull(discovered);
	}
}