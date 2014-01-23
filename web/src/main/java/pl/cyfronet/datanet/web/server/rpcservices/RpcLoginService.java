package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ax.FetchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	public static final String USER_ROLE = "ROLE_USER";

	@Autowired private HibernateUserDao userDao;
	@Autowired private ConsumerManager openIdManager;

	@Value("${open.id.identifier.prefix}") private String openIdIdentifierPrefix;

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
			@SuppressWarnings("unchecked")
			List<DiscoveryInformation> discoveries = openIdManager.discover(openIdIdentifierPrefix + openIdLogin);
			DiscoveryInformation discovered = openIdManager.associate(discoveries);
			HttpSession httpSession = WebSessionHelper.getCurrentSession();
			httpSession.setAttribute(OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME, discovered);
			
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
			throw new LoginException(Code.OpenIdAssociationFailed);
		}
	}

	@Override
	public String retrieveUserProxy() {
		String proxy = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		
		if (proxy != null) {
			String base64Proxy = Base64.encodeBase64String(proxy.getBytes()).replaceAll("\n", "");
			
			return base64Proxy;
		}
		
		return null;
	}
}