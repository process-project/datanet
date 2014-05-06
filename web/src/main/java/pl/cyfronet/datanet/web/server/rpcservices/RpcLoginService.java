package pl.cyfronet.datanet.web.server.rpcservices;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.html.HtmlResolver;
import org.openid4java.discovery.yadis.YadisResolver;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.server.RealmVerifierFactory;
import org.openid4java.util.HttpFetcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.util.WebSessionHelper;

@Service("loginService")
public class RpcLoginService implements LoginService {
	private static final Logger log = LoggerFactory.getLogger(RpcLoginService.class);
	
	public static final String OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME = "cyfronet.datanet.openid.discoveries";
	public static final String OPEN_ID_CONSUMER_MANAGER = "cyfronet.datanet.openid.consumer.manager";
	public static final String USER_ROLE = "ROLE_USER";

	@Autowired private HibernateUserDao userDao;

	@Value("${open.id.identifier.prefix}") private String openIdIdentifierPrefix;
	@Value("${open.id.allowed.host}") private String openIdAllowedHost;
	@Value("classpath:openid_issuer_cert.pem") private Resource issuerCert;

	@Override
	public boolean isUserLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean result = authentication != null && authentication.isAuthenticated() &&
				!authentication.getName().equals("anonymousUser") &&
				!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS")) &&
				authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"));
		log.debug("User login status is: {}", result);
		
		return result;
	}

	@Override
	public void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Override
	public String initiateOpenIdLogin(String openIdLogin) throws LoginException {
		log.info("Initialising OpenID login procedure for user {}", openIdLogin);

		try {
			ConsumerManager openIdManager = createConsumerManager();
			@SuppressWarnings("unchecked")
			List<DiscoveryInformation> discoveries = openIdManager.discover(openIdIdentifierPrefix + openIdLogin);
			DiscoveryInformation discovered = openIdManager.associate(discoveries);
			HttpSession httpSession = WebSessionHelper.getCurrentSession();
			httpSession.setAttribute(OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME, discovered);
			httpSession.setAttribute(OPEN_ID_CONSUMER_MANAGER, openIdManager);
			
			String openIdReturnUrl = WebSessionHelper.getCurrentUrl();
			log.debug("Return URL for OpenID association request is {}", openIdReturnUrl);
			
			AuthRequest authReq = openIdManager.authenticate(discovered, openIdReturnUrl);
			FetchRequest fetchRequest = FetchRequest.createFetchRequest();
			fetchRequest.addAttribute("email", "http://schema.openid.net/contact/email", true);
			fetchRequest.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
			fetchRequest.addAttribute("proxy", "http://openid.plgrid.pl/certificate/proxy ", false);
			fetchRequest.addAttribute("userCert", "http://openid.plgrid.pl/certificate/userCert", false);
			fetchRequest.addAttribute("proxyPrivKey", "http://openid.plgrid.pl/certificate/proxyPrivKey", false);
			authReq.addExtension(fetchRequest);
			
			return authReq.getDestinationUrl(true);
		} catch (Exception e) {
			log.error("Could not properly initiate OpenID authentication", e);
			throw new LoginException(Code.OpenIdAssociationFailed);
		}
	}

	private ConsumerManager createConsumerManager() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] tma = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] certs,	String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,	String authType) throws CertificateException {
				PublicKey issuerKey = null;
				
				try {
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					X509Certificate issuerX509Cert = (X509Certificate) cf.generateCertificate(issuerCert.getInputStream());
					issuerKey = issuerX509Cert.getPublicKey();
				} catch (CertificateException | IOException e) {
					String msg = "Could not read issuer certificate to perform verification";
					log.warn(msg);
					
					throw new CertificateException(msg);
				}
				
				if(issuerKey != null) {
					X509Certificate serverCert = null;
					
					for(X509Certificate cert : certs) {
						if(cert.getSubjectDN().getName() != null && cert.getSubjectDN().getName().contains(openIdAllowedHost)) {
							serverCert = cert;
							
							break;
						}
					}
					
					if(serverCert != null) {
						try {
							serverCert.verify(issuerKey);
							log.debug("OpenID provider certificate validation successful");
						} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
							String msg = "Presented certificate is not valid.";
							log.warn(msg);
							
							throw new CertificateException(msg);
						}
					} else {
						String msg = "Valid server certificate could not be found to perform verification";
						log.warn(msg);
						
						throw new CertificateException(msg);
					}
				} else {
					String msg = "Could not read issuer public key to perform verification";
					log.warn(msg);
					
					throw new CertificateException(msg);
				}
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, tma, new java.security.SecureRandom());

		HttpFetcherFactory hff = new HttpFetcherFactory(sc, new X509HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return verify(hostname);
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				if(!verify(host)) {
					throw new SSLException("Host " + host + " is not allowed.");
				}
			}
			
			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {
				if(!verify(host)) {
					throw new SSLException("Host " + host + " is not allowed.");
				}
			}
			
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
				if(!verify(host)) {
					throw new SSLException("Host " + host + " is not allowed.");
				}
			}
			
			private boolean verify(String hostname) {
				boolean result = openIdAllowedHost != null && openIdAllowedHost.equals(hostname);
				
				if(!result) {
					log.warn("OpenID host verification failed for {}. Allowed host: {}",
							hostname, openIdAllowedHost);
				}
				
				return result;
			}
		});
		YadisResolver yr = new YadisResolver(hff);
		RealmVerifierFactory rvf = new RealmVerifierFactory(yr);
		Discovery d = new Discovery(new HtmlResolver(hff), yr,
				Discovery.getXriResolver());
		ConsumerManager consumerManager = new ConsumerManager(rvf, d, hff);
		consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
		consumerManager.setNonceVerifier(new InMemoryNonceVerifier(5000));
		
		return consumerManager;
	}
}