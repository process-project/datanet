package pl.cyfronet.datanet.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NotificationEventHandler extends EventHandler {
	void onNotificationEvent(NotificationEvent event);
}
