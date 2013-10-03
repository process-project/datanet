package pl.cyfronet.datanet.web.client.di;

import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutAwareRpcRequestBuilder;
import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutController;
import pl.cyfronet.datanet.web.client.di.factory.EntityDataPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.di.factory.EntityPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.di.factory.EntityRowDataProviderFactory;
import pl.cyfronet.datanet.web.client.di.factory.FieldPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.di.factory.ModelActivityFactory;
import pl.cyfronet.datanet.web.client.di.factory.ModelPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.di.factory.RepositoryActivityFactory;
import pl.cyfronet.datanet.web.client.di.factory.VersionActivityFactory;
import pl.cyfronet.datanet.web.client.di.provider.LoginServiceProvider;
import pl.cyfronet.datanet.web.client.di.provider.ModelServiceProvider;
import pl.cyfronet.datanet.web.client.di.provider.PlaceControllerProvider;
import pl.cyfronet.datanet.web.client.di.provider.RepositoryServiceProvider;
import pl.cyfronet.datanet.web.client.di.provider.SessionTimeoutControllerProvider;
import pl.cyfronet.datanet.web.client.di.provider.VersionServiceProvider;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.mvp.AppPlaceHistoryMapper;
import pl.cyfronet.datanet.web.client.mvp.activity.ModelActivity;
import pl.cyfronet.datanet.web.client.mvp.activity.RepositoryActivity;
import pl.cyfronet.datanet.web.client.mvp.activity.VersionActivity;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.services.VersionServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityRowDataProvider;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanel;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPanel;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelWidget;

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
		bind(VersionController.class).in(Singleton.class);
		bind(RepositoryController.class).in(Singleton.class);
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(AppPlaceHistoryMapper.class).in(Singleton.class);
		bind(SessionTimeoutController.class).toProvider(SessionTimeoutControllerProvider.class).in(Singleton.class);
		bind(SessionTimeoutAwareRpcRequestBuilder.class).in(Singleton.class);

		configureAsyncServices();
		configureViews();
		configureActivities();
		configurePresenterFactories();
		configureModelPresenterFactories();		
		configureDataProviderFactories();
	}

	private void configureAsyncServices() {
		bind(LoginServiceAsync.class).toProvider(LoginServiceProvider.class).in(Singleton.class);
		bind(ModelServiceAsync.class).toProvider(ModelServiceProvider.class).in(Singleton.class);
		bind(RepositoryServiceAsync.class).toProvider(RepositoryServiceProvider.class).in(Singleton.class);
		bind(VersionServiceAsync.class).toProvider(VersionServiceProvider.class).in(Singleton.class);
	}

	private void configureViews() {
		bind(TopNavPresenter.View.class).to(TopNavPanel.class);
		bind(ModelTreePanelPresenter.View.class).to(ModelTreePanel.class);
		bind(RepositoryPanelPresenter.View.class).to(RepositoryPanelWidget.class);
		bind(EntityDataPanelPresenter.View.class).to(EntityDataPanelWidget.class);
		bind(ModelPanelPresenter.View.class).to(ModelPanelWidget.class);
		bind(EntityPanelPresenter.View.class).to(EntityPanelWidget.class);
		bind(FieldPanelPresenter.View.class).to(FieldPanelWidget.class);
		bind(VersionPanelPresenter.View.class).to(VersionPanelWidget.class);
	}

	private void configureActivities() {
		install(new GinFactoryModuleBuilder().implement(Activity.class,
				ModelActivity.class).build(ModelActivityFactory.class));
		install(new GinFactoryModuleBuilder().implement(Activity.class,
				VersionActivity.class).build(VersionActivityFactory.class));
		install(new GinFactoryModuleBuilder().implement(Activity.class,
				RepositoryActivity.class).build(RepositoryActivityFactory.class));		
	}
	
	private void configurePresenterFactories() {
		install(new GinFactoryModuleBuilder().implement(EntityDataPanelPresenter.class,
				EntityDataPanelPresenter.class).build(EntityDataPanelPresenterFactory.class));
	}
	
	private void configureModelPresenterFactories() {
		install(new GinFactoryModuleBuilder().implement(ModelPanelPresenter.class,
				ModelPanelPresenter.class).build(ModelPanelPresenterFactory.class));
		install(new GinFactoryModuleBuilder().implement(EntityPanelPresenter.class,
				EntityPanelPresenter.class).build(EntityPanelPresenterFactory.class));
		install(new GinFactoryModuleBuilder().implement(FieldPanelPresenter.class,
				FieldPanelPresenter.class).build(FieldPanelPresenterFactory.class));		
	}

	private void configureDataProviderFactories() {
		install(new GinFactoryModuleBuilder().implement(EntityRowDataProvider.class,
				EntityRowDataProvider.class).build(EntityRowDataProviderFactory.class));
	}
}