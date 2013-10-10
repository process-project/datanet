package pl.cyfronet.datanet.web.client.widgets.topnav;

import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPanel.MessageType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

public class TopNavPresenter implements Presenter {
	interface TopNavEventBinder extends EventBinder<TopNavPresenter> {}
	private final TopNavEventBinder eventBinder = GWT.create(TopNavEventBinder.class);
	
	public interface View {
		void setPresenter(Presenter presenter);
		void displayMessage(String message, MessageType type);
		void selectPlLocales();
		void selectEnLocales();
	}

	private Provider<ClientController> clientController;
	private View view;
	private NotificationMessages notificationMessages;

	@Inject
	public TopNavPresenter(View view, EventBus eventBus, Provider<ClientController> clientController,
			NotificationMessages notificationMessages) {
		eventBinder.bindEventHandlers(this, eventBus);
		
		this.view = view;
		this.clientController = clientController;
		this.notificationMessages = notificationMessages;		
		
		view.setPresenter(this);		
		initLocalesButtons();
	}

	private void initLocalesButtons() {
		if("pl".equals(LocaleInfo.getCurrentLocale().getLocaleName())) {
			view.selectPlLocales();
		} else {
			view.selectEnLocales();
		}
	}

	@Override
	public void onLogout() {
		clientController.get().onLogout();
	}

	@Override
	public void onSwitchLocale(String locale) {
		clientController.get().switchLocale(locale);
	}

	@Override
	public void onHelp() {
		String path = Window.Location.getPath();

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		Window.open(Window.Location.createUrlBuilder().setPath(path + "/docs/manual/")
				.buildString(), "_blank", "");
	}

	@EventHandler
	void onNotification(NotificationEvent event) {
		String message = getMessage(event.getMessageCode(), event.getMessageParams());
		view.displayMessage(message, getType(event));
	}
	
	private MessageType getType(NotificationEvent event) {
		return event.getType() == NotificationType.ERROR ? MessageType.ERROR
				: MessageType.INFO;
	}
	
	@Override
	public Widget getWidget() {
		return (Widget) view;
	}
	
	private String getMessage(String messageCode, String[] messageParams) {
		String message = notificationMessages.getString(messageCode);
		
		if(messageParams != null && messageParams.length > 0) {
			for(int i = 0; i < messageParams.length; i++) {
				String placeholder = "{" + i + "}";
				
				if(message.contains(placeholder)) {
					message = message.replace(placeholder, String.valueOf(messageParams[i]));
				}
			}
		}
		
		return message;
	}
}
