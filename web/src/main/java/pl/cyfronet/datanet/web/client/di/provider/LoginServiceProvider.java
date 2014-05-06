package pl.cyfronet.datanet.web.client.di.provider;

import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutAndCsrfAwareRpcRequestBuilder;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LoginServiceProvider implements Provider<LoginServiceAsync> {
	private SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder;

	@Inject
	public LoginServiceProvider(SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}
	
	@Override
	public LoginServiceAsync get() {
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		((ServiceDefTarget) loginService).setRpcRequestBuilder(requestBuilder);
		
		//this is used to break the injection cycle which would exist in normal constructor injection
		requestBuilder.setLoginService(loginService);
		
		return loginService;
	}
}