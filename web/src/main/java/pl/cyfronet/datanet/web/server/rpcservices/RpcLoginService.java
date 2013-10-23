package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.services.security.PortalAuthenticationManager;
import pl.cyfronet.datanet.web.server.util.WebSessionHelper;

@Service("loginService")
public class RpcLoginService implements LoginService {
	private static final Logger log = LoggerFactory.getLogger(RpcLoginService.class);
	
	public static final String OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME = "cyfronet.datanet.openid.discoveries";
	
	@Autowired private PortalAuthenticationManager authenticationManager;
	@Autowired private HibernateUserDao userDao;
	@Autowired private ConsumerManager openIdManager;

	@Value("${open.id.identifier.prefix}") private String openIdIdentifierPrefix;

	@Override
	public void login(String userLogin, String password) throws LoginException {
		try {
			Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(userLogin, password);
			Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
			SecurityContextHolder.getContext().setAuthentication(authenticationResult);
			log.info("User {} successfully logged in", userLogin);
			
			UserDbEntity user = userDao.getUser(userLogin);
			
			if(user == null) {
				user = new UserDbEntity();
				user.setLogin(userLogin);
			}
			
			user.setLastLogin(Calendar.getInstance().getTime());
			userDao.saveUser(user);
		} catch (Exception e) {
			log.error("There was an authentication problem", e);
			
			if(e.getCause() != null && e.getCause() instanceof LoginException) {
				throw (LoginException) e.getCause();
			} else {
				throw new LoginException(Code.Unknown);
			}
		}
	}

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
			List<DiscoveryInformation> discoveries = openIdManager.discover(openIdIdentifierPrefix + openIdLogin);
			DiscoveryInformation discovered = openIdManager.associate(discoveries);
			HttpSession httpSession = WebSessionHelper.getCurrentSession();
			httpSession.setAttribute(OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME, discovered);
			
			String openIdReturnUrl = WebSessionHelper.getCurrentUrl();
			AuthRequest authReq = openIdManager.authenticate(discovered, openIdReturnUrl);
			FetchRequest fetchRequest = FetchRequest.createFetchRequest();
			fetchRequest.addAttribute("email", "http://schema.openid.net/contact/email", true);
			fetchRequest.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
			fetchRequest.addAttribute("proxy", "http://openid.plgrid.pl/certificate/proxy ", false);
			fetchRequest.addAttribute("userCert", "http://openid.plgrid.pl/certificate/userCert", false);
//			fetchRequest.addAttribute("proxyPrivKey", "http://openid.plgrid.pl/certificate/proxyPrivKey", false);
			authReq.addExtension(fetchRequest);
			
			return authReq.getDestinationUrl(true);
		} catch (DiscoveryException | MessageException | ConsumerException e) {
			throw new LoginException(Code.OpenIdAssociationFailed);
		}
	}
}