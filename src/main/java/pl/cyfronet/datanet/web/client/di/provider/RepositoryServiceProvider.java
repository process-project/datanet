package pl.cyfronet.datanet.web.client.di.provider;

import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutAndCsrfAwareRpcRequestBuilder;
import pl.cyfronet.datanet.web.client.services.RepositoryService;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class RepositoryServiceProvider implements Provider<RepositoryServiceAsync> {
	private SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder;
	
	@Inject
	public RepositoryServiceProvider(SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}

	@Override
	public RepositoryServiceAsync get() {
		RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);
		((ServiceDefTarget) repositoryService).setRpcRequestBuilder(requestBuilder);
		
		return repositoryService;
	}
}