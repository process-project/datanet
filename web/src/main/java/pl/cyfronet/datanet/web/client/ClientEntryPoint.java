package pl.cyfronet.datanet.web.client;

import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.di.DatanetGinjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class ClientEntryPoint implements EntryPoint {
	@Override
	public void onModuleLoad() {
		DatanetGinjector injector = GWT.create(DatanetGinjector.class);
		ClientController clientController = injector.getClientController();
		clientController.start();
	}
}