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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((messageCode == null) ? 0 : messageCode.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationEvent other = (NotificationEvent) obj;
		if (messageCode == null) {
			if (other.messageCode != null)
				return false;
		} else if (!messageCode.equals(other.messageCode))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}