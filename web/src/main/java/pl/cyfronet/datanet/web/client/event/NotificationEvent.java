package pl.cyfronet.datanet.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NotificationEvent extends GwtEvent<NotificationEventHandler> {
	public static Type<NotificationEventHandler> TYPE = new Type<NotificationEventHandler>();
	
	public enum NotificationType {
		SUCCESS, WARNING, ERROR, NOTE
	}

	private NotificationType type;
	private String message;

	public NotificationEvent(String message, NotificationType type) {
		this.message = message;
		this.type = type;
	}

	@Override
	public Type<NotificationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NotificationEventHandler handler) {
		handler.onNotificationEvent(this);
	}

	public NotificationType getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
}