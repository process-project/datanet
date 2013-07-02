package pl.cyfronet.datanet.web.client.di;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.di.factory.ModelActivityFactory;
import pl.cyfronet.datanet.web.client.di.provider.PlaceControllerProvider;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.mvp.AppPlaceHistoryMapper;
import pl.cyfronet.datanet.web.client.mvp.activity.ModelActivity;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanel;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPanel;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class DatanetClientModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(ClientController.class).in(Singleton.class);
		bind(ModelController.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class)
				.in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(AppPlaceHistoryMapper.class).in(
				Singleton.class);

		configureViews();
		configureActivities();
	}

	private void configureViews() {
		bind(TopNavPresenter.View.class).to(TopNavPanel.class);
		bind(ModelTreePanelPresenter.View.class).to(ModelTreePanel.class);
	}

	private void configureActivities() {
		install(new GinFactoryModuleBuilder().implement(Activity.class,
				ModelActivity.class).build(ModelActivityFactory.class));
	}
}