package pl.cyfronet.datanet.web.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pl.cyfronet.datanet.web.client.errors.LoginException;

@RemoteServiceRelativePath("../rpcservices/loginService")
public interface LoginService extends RemoteService {
	boolean isUserLoggedIn();
	void logout();
	String initiateOpenIdLogin(String openIdLogin) throws LoginException;
	String retrieveUserProxy();
}