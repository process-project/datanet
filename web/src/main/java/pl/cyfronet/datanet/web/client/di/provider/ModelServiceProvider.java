package pl.cyfronet.datanet.web.client.di.provider;

import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutAndCsrfAwareRpcRequestBuilder;
import pl.cyfronet.datanet.web.client.services.ModelService;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ModelServiceProvider implements Provider<ModelServiceAsync> {
	private SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder;
	
	@Inject
	public ModelServiceProvider(SessionTimeoutAndCsrfAwareRpcRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
		
	}
	@Override
	public ModelServiceAsync get() {
		ModelServiceAsync modelService = GWT.create(ModelService.class);
		((ServiceDefTarget) modelService).setRpcRequestBuilder(requestBuilder);
		
		return modelService;
	}
}