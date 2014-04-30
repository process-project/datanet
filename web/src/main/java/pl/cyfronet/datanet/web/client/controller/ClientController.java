package pl.cyfronet.datanet.web.client.controller;

import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutController;
import pl.cyfronet.datanet.web.client.event.notification.GenericNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.layout.MainLayout;
import pl.cyfronet.datanet.web.client.mvp.AppActivityMapper;
import pl.cyfronet.datanet.web.client.mvp.WestActivityMapper;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ClientController {
	private static String userProxy;
	
	private LoginServiceAsync loginService;
	private TopNavPresenter topNavPresenter;
	private PlaceController placeController;
	private EventBus eventBus;
	private PlaceHistoryMapper historyMapper;
	private AppActivityMapper appActivityMapper;
	private WestActivityMapper westActivityMapper;
	private SessionTimeoutController sessionTimeoutController;

	@Inject
	public ClientController(EventBus eventBus,
			AppActivityMapper appActivityMapper,
			TopNavPresenter topNavPresenter,
			WestActivityMapper westActivityManager,
			PlaceController placeController, PlaceHistoryMapper historyMapper,
			LoginServiceAsync loginService,
			SessionTimeoutController sessionTimeoutController) {
		this.eventBus = eventBus;
		this.appActivityMapper = appActivityMapper;
		this.topNavPresenter = topNavPresenter;
		this.westActivityMapper = westActivityManager;
		this.placeController = placeController;
		this.historyMapper = historyMapper;
		this.loginService = loginService;
		this.sessionTimeoutController = sessionTimeoutController;
	}
	
	public static String getUserProxy() {
		return userProxy;
	}

	public void start() {
		loginService.isUserLoggedIn(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean isLoggedIn) {
				if (!isLoggedIn) {
					showLoginPanel();
				} else {
					loginService.retrieveUserProxy(new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable t) {
							eventBus.fireEvent(new NotificationEvent(
									GenericNotificationMessage.rpcError,
									NotificationType.ERROR, t.getLocalizedMessage()));
						}

						@Override
						public void onSuccess(String userProxy) {
							ClientController.userProxy = userProxy;
							sessionTimeoutController.start();
							showMainPanel();
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable t) {
				eventBus.fireEvent(new NotificationEvent(
						GenericNotificationMessage.rpcError,
						NotificationType.ERROR, t.getLocalizedMessage()));
			}
		});
	}

	public void onLogin() {
		sessionTimeoutController.start();
		showMainPanel();
	}

	public void onLogout() {
		loginService.logout(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable t) {
				eventBus.fireEvent(new NotificationEvent(
						GenericNotificationMessage.rpcError,
						NotificationType.ERROR, t.getLocalizedMessage()));
			}

			@Override
			public void onSuccess(Void v) {
				sessionTimeoutController.cancel();
				showLoginPanel();
			}
		});
	}

	public void switchLocale(String locale) {
		UrlBuilder builder = Window.Location.createUrlBuilder();
		builder.setParameter("locale", locale);
		Window.Location.assign(builder.buildString());
	}

	private void showMainPanel() {
		MainLayout layout = new MainLayout();
		layout.getHeaderContainer().setWidget(topNavPresenter.getWidget());

		// activities & places
		// center
		ActivityManager activityManager = new ActivityManager(
				appActivityMapper, eventBus);
		activityManager.setDisplay(layout.getCenterContainer());

		// west
		ActivityManager westActivityManager = new ActivityManager(
				westActivityMapper, eventBus);
		westActivityManager.setDisplay(layout.getWestContainer());

		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
				historyMapper);
		historyHandler.register(placeController, eventBus, new WelcomePlace());

		clearPanels();
		RootPanel.get().add(RootLayoutPanel.get());
		RootLayoutPanel.get().add(layout);
		historyHandler.handleCurrentHistory();
	}

	private void showLoginPanel() {
		LoginPresenter loginPresenter = new LoginPresenter(loginService,
				new LoginWidget(), this);
		clearPanels();
		RootPanel.get().add(loginPresenter.getWidget());
		loginPresenter.showExternalErrors();
	}

	private void clearPanels() {
		if (RootLayoutPanel.get().getWidgetCount() > 0) {
			RootLayoutPanel.get().clear();
		}

		if (RootPanel.get().getWidgetCount() > 0) {
			RootPanel.get().clear();
		}
	}
}