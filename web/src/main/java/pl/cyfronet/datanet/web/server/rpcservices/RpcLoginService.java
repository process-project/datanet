package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.security.PortalAuthenticationManager;
import pl.cyfronet.datanet.web.server.util.SpringSessionHelper;

@Service("loginService")
public class RpcLoginService implements LoginService {
	private static final Logger log = LoggerFactory.getLogger(RpcLoginService.class);
	
	@Autowired private PortalAuthenticationManager authenticationManager;
	@Autowired private HibernateUserDao userDao;

	@Override
	public void login(String userLogin, String password) throws LoginException {
		try {
			Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(userLogin, password);
			Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
			SecurityContextHolder.getContext().setAuthentication(authenticationResult);
			SpringSessionHelper.setSecurityContext(SecurityContextHolder.getContext());
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
		SecurityContext securityContext = (SecurityContext) SpringSessionHelper.getHttpSession().getAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		boolean result = securityContext != null && securityContext.getAuthentication() != null &&
				securityContext.getAuthentication().isAuthenticated();
		log.debug("User login status is: {}", result);
		
		return result;
	}

	@Override
	public void logout() {
		SpringSessionHelper.getHttpSession().removeAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
	}
}