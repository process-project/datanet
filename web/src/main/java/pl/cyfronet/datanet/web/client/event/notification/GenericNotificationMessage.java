package pl.cyfronet.datanet.web.client.event.notification;

public enum GenericNotificationMessage implements NotificationMessage{
	rpcError
	;
	
	@Override
	public String getMessageCode() {
		return name();
	}
}
