package pl.cyfronet.datanet.web.client;

import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.services.ModelService;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.services.RepositoryService;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class ClientEntryPoint implements EntryPoint {
	@Override
	public void onModuleLoad() {
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		ModelServiceAsync modelService = GWT.create(ModelService.class);
		RepositoryServiceAsync repositoryService = GWT.create(RepositoryService.class);
		RpcErrorHandler rpcErrorHandler = new RpcErrorHandler();
		AppProperties properties = GWT.create(AppProperties.class);
		ModelValidator modelValidator = new ModelValidator();
		ClientController clientController = new ClientController(loginService,
				modelService, repositoryService, rpcErrorHandler, modelValidator);
		clientController.start();
	}
}