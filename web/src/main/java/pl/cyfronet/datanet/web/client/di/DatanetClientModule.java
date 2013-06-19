package pl.cyfronet.datanet.web.client.di;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanel;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPanel;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class DatanetClientModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(ClientController.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		
		configureViews();
	}

	private void configureViews() {
		bind(TopNavPresenter.View.class).to(TopNavPanel.class);
		bind(ModelTreePanelPresenter.View.class).to(ModelTreePanel.class);
	}
}