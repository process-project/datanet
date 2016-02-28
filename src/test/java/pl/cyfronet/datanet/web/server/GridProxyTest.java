package pl.cyfronet.datanet.web.server;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.proxy.ProxyUtils;

public class GridProxyTest {
	private static final Logger log = LoggerFactory.getLogger(GridProxyTest.class);
	
	@Test
	public void testGridProxyParsing() throws CertificateException {
		String proxy = "-----BEGIN CERTIFICATE-----<br>\n"
				+ "MIIC+zCCAeOgAwIBAgIIGYYg85BsUXIwDQYJKoZIhvcNAQEFBQAwdzELMAkGA1UE\n"
				+ "<br>BhMCUEwxEDAOBgNVBAoTB1BMLUdyaWQxEzARBgNVBAoTClV6eXRrb3duaWsxETAP\n"
				+ "<br>BgNVBAoTCENZRlJPTkVUMRgwFgYDVQQDEw9EYW5pZWwgSGFyZXpsYWsxFDASBgNV\n"
				+ "<br>BAMTC3BsZ2hhcmV6bGFrMB4XDTEzMTAxODEwNDU1MloXDTEzMTAxOTEwNTA1Mlow\n"
				+ "<br>gYcxCzAJBgNVBAYTAlBMMRAwDgYDVQQKEwdQTC1HcmlkMRMwEQYDVQQKEwpVenl0\n"
				+ "<br>a293bmlrMREwDwYDVQQKEwhDWUZST05FVDEYMBYGA1UEAxMPRGFuaWVsIEhhcmV6\n"
				+ "<br>bGFrMRQwEgYDVQQDEwtwbGdoYXJlemxhazEOMAwGA1UEAxMFcHJveHkwgZ8wDQYJ\n"
				+ "<br>KoZIhvcNAQEBBQADgY0AMIGJAoGBAIrtVujNJhDz6EEwyf3GzPR9kafk5i+Uaa29\n"
				+ "<br>EwYZhHgJlHG4A4IXumdQz13fVIxiIbLffVUmhhw1oI14scY0wqyY7zAmWs7gYv0P\n"
				+ "<br>ZymVhK8k/v/ufOFcXcl0HonPhnlggRPRnM/NgTSXZYlcSVod2uRH9WoE9b7jKqq0\n"
				+ "<br>b880DQdnAgMBAAEwDQYJKoZIhvcNAQEFBQADggEBAHlOpySet6YQxozUJDlcDT8h\n"
				+ "<br>FPO/S7nixRY+ViTmOL87bR1Himyjx57Xx9H4NvyxVkHB58np4fN2gQUwdNIIRJME\n"
				+ "<br>zXN/lmDOe1er18QtvJ9gN0ikrBFKS5s8TIYQq1Z++1HIpbacDZOb7bYATLqCGuqc\n"
				+ "<br>S/XRkkjeDMyoPrDZ17ABOqUhW9JiqVlixO1IZUm6wVzXPVSXhGPz0J/9SlcdwWSI\n"
				+ "<br>knEOPGA01w3j9hgFFGM8Zxm7kFLQAS7zo0W3LYASb0Sh4MkmSo9tq8A4QOEPxkqP\n"
				+ "<br>I10udYtxV+WXZ4TaMO7I2NGRUA5TRInEGQBNja9O/uGYsGthOtQqvLqi9zIMsA0=\n"
				+ "<br>-----END CERTIFICATE-----";
		ByteArrayInputStream baos = new ByteArrayInputStream(proxy.replace("<br>", "").getBytes());
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate crt = (X509Certificate) cf.generateCertificate(baos);
		boolean result = ProxyUtils.isProxy(crt);
		log.debug("Is proxy: {}", result);
		log.debug("Issuer DN: {}", crt.getIssuerDN());
		log.debug("Not before: {}", crt.getNotBefore());
		log.debug("Not after: {}", crt.getNotAfter());
	}
}