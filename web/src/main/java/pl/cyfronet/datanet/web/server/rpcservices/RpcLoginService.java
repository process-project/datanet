package pl.cyfronet.datanet.web.server.rpcservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.server.services.PortalLoginHandler;

@Service("loginService")
public class RpcLoginService implements LoginService {
	private static final Logger log = LoggerFactory.getLogger(RpcLoginService.class);
	
	@Autowired private PortalLoginHandler portalLoginHandler;
	
	@Override
	public void login(String user, String password) throws LoginException {
		portalLoginHandler.login("dummy", "dummy");
		throw new LoginException();
	}

	@Override
	public boolean isUserLoggedIn() {
		return false;
	}
}