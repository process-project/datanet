package pl.cyfronet.datanet.test.mock.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;

public class NotificationEvenMatcher extends BaseMatcher<NotificationEvent> {

	private NotificationType type;

	public NotificationEvenMatcher(NotificationType type) {
		this.type = type;
	}

	@Override
	public boolean matches(Object item) {
		if (item instanceof NotificationEvent) {
			NotificationEvent event = (NotificationEvent) item;
			return event.getType() == type;
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {

	}

}
