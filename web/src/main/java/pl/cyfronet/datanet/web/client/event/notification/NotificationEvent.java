package pl.cyfronet.datanet.web.client.event.notification;


import com.google.gwt.event.shared.GwtEvent;

public class NotificationEvent extends GwtEvent<NotificationEventHandler> {
	public static Type<NotificationEventHandler> TYPE = new Type<NotificationEventHandler>();
	
	public enum NotificationType {
		SUCCESS, WARNING, ERROR, NOTE
	}

	private NotificationType type;
	private NotificationMessage messageCode;
	private String[] messageParams;

	public NotificationEvent(NotificationMessage messageCode, NotificationType type) {
		this.messageCode = messageCode;
		this.type = type;
	}
	
	public NotificationEvent(NotificationMessage messageCode, NotificationType type, String ... messageParams) {
		this(messageCode, type);
		this.messageParams = messageParams;
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

	public String getMessageCode() {
		return messageCode.getMessageCode();
	}
	
	public String[] getMessageParams() {
		return messageParams;
	}
}