package pl.cyfronet.datanet.web.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	void isUserLoggedIn(AsyncCallback<Boolean> callback);
	void logout(AsyncCallback<Void> asyncCallback);
	void initiateOpenIdLogin(String openIdLogin, AsyncCallback<String> asyncCallback);
	void retrieveUserProxy(AsyncCallback<String> asyncCallback);
}