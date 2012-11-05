package pl.cyfronet.datanet.web.client;

import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginService;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class ClientEntryPoint implements EntryPoint {
	@Override
	public void onModuleLoad() {
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		RpcErrorHandler rpcErrorHandler = new RpcErrorHandler();
		ClientController clientController = new ClientController(loginService, rpcErrorHandler);
		clientController.start();
	}
}