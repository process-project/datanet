package pl.cyfronet.datanet.web.client.di;

import pl.cyfronet.datanet.web.client.controller.ClientController;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(DatanetClientModule.class)
public interface DatanetGinjector extends Ginjector {
	ClientController getClientController();
}