package pl.cyfronet.datanet.web.client.widgets.topnav;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.event.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.NotificationEventHandler;
import pl.cyfronet.datanet.web.client.messages.MessageDispatcher;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

public class TopNavPresenter implements Presenter, MessageDispatcher {
	public interface View {
		void setPresenter(Presenter presenter);

		void displayMessage(String message, MessageType type);
	}

	private Provider<ClientController> clientController;
	
	private View view;

	@Inject
	public TopNavPresenter(View view, EventBus eventBus, Provider<ClientController> clientController) {
		this.view = view;
		this.clientController = clientController;
		
		view.setPresenter(this);		
		
		eventBus.addHandler(NotificationEvent.TYPE,
				new NotificationEventHandler() {
					@Override
					public void onNotificationEvent(NotificationEvent event) {
						TopNavPresenter.this.view.displayMessage(
								event.getMessage(), getType(event));
					}

					private MessageType getType(NotificationEvent event) {
						return event.getType() == NotificationType.ERROR ? MessageType.ERROR
								: MessageType.INFO;
					}
				});
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

		Window.open(Window.Location.createUrlBuilder().setPath(path + "/help")
				.buildString(), "_blank", "");
	}

	@Override
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public void displayMessage(String message, MessageType type) {
		view.displayMessage(message, type);
	}
}
