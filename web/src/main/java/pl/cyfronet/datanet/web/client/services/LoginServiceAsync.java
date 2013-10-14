package pl.cyfronet.datanet.web.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void login(String user, String password, AsyncCallback<Void> callback);
	void isUserLoggedIn(AsyncCallback<Boolean> callback);
	void logout(AsyncCallback<Void> asyncCallback);
	void initiateOpenIdLogin(String openIdLogin, AsyncCallback<String> asyncCallback);
}