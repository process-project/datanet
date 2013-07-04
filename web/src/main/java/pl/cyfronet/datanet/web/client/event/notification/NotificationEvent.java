package pl.cyfronet.datanet.web.client.event.notification;


import com.google.web.bindery.event.shared.binder.GenericEvent;

public class NotificationEvent extends GenericEvent {
	
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