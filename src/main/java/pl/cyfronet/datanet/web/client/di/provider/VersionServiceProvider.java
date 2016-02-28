package pl.cyfronet.datanet.web.client.di.provider;

import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutAndCsrfAwareRpcRequestBuilder;
import pl.cyfronet.datanet.web.client.services.VersionService;
import pl.cyfronet.datanet.web.client.services.VersionServiceAsync;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class VersionServiceProvider implements Provider<VersionServiceAsync> {
	private SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder;

	@Inject
	public VersionServiceProvider(SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}
	
	@Override
	public VersionServiceAsync get() {
		VersionServiceAsync versionService = GWT.create(VersionService.class);
		((ServiceDefTarget) versionService).setRpcRequestBuilder(requestBuilder);
		
		return versionService;
	}
}