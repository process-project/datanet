package pl.cyfronet.datanet.web.client.di;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class DatanetClientModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(ClientController.class).in(Singleton.class);
	}
}