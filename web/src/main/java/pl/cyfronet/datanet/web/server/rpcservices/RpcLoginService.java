package pl.cyfronet.datanet.web.server.rpcservices;

import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.services.LoginService;

@Service("loginService")
public class RpcLoginService implements LoginService {
	@Override
	public void login(String user, String password) throws LoginException {
		throw new LoginException();
	}

	@Override
	public boolean isUserLoggedIn() {
		return false;
	}
}