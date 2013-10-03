package pl.cyfronet.datanet.web.client.di.provider;

import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutAwareRpcRequestBuilder;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LoginServiceProvider implements Provider<LoginServiceAsync> {
	private SessionTimeoutAwareRpcRequestBuilder requestBuilder;

	@Inject
	public LoginServiceProvider(SessionTimeoutAwareRpcRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
		
	}
	
	@Override
	public LoginServiceAsync get() {
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		((ServiceDefTarget) loginService).setRpcRequestBuilder(requestBuilder);
		
		return loginService;
	}
}