package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.services.portallogin.PortalLoginHandler;

@Service("loginService")
public class RpcLoginService implements LoginService {
	private static final Logger log = LoggerFactory.getLogger(RpcLoginService.class);
	
	@Autowired private PortalLoginHandler portalLoginHandler;
	@Autowired private HibernateUserDao userDao;
	
	@Override
	public void login(String userLogin, String password) throws LoginException {
		try {
			portalLoginHandler.login(userLogin, password);
			log.info("User {} successfully logged in", userLogin);
			UserDbEntity user = userDao.getUser(userLogin);
			
			if(user == null) {
				user = new UserDbEntity();
				user.setLogin(userLogin);
			}
			
			user.setLastLogin(Calendar.getInstance().getTime());
			userDao.saveUser(user);
			
			RequestContextHolder.getRequestAttributes().
					setAttribute("authentication", "true", RequestAttributes.SCOPE_SESSION);
			RequestContextHolder.getRequestAttributes().
					setAttribute("userLogin", userLogin, RequestAttributes.SCOPE_SESSION);
		} catch (Exception e) {
			if(e instanceof LoginException) {
				throw e;
			} else {
				throw new LoginException(Code.Unknown);
			}
		}
	}

	@Override
	public boolean isUserLoggedIn() {
		boolean result = false;
		
		if(RequestContextHolder.getRequestAttributes().
				getAttribute("authentication", RequestAttributes.SCOPE_SESSION) != null) {
			result = true;
		}
		
		log.debug("User login status is: {}", result);
		
		return result;
	}

	@Override
	public void logout() {
		RequestContextHolder.getRequestAttributes().
				removeAttribute("authentication", RequestAttributes.SCOPE_SESSION);
		RequestContextHolder.getRequestAttributes().
				removeAttribute("userLogin", RequestAttributes.SCOPE_SESSION);
	}
}