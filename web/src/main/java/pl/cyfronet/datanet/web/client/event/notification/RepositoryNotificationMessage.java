package pl.cyfronet.datanet.web.client.event.notification;

public enum RepositoryNotificationMessage implements NotificationMessage {
	repositoryDeployed,
	repositoryNotPresent,
	repositoryLoadError,
	repositoryEntityDataLoadError,
	;

	@Override
	public String getMessageCode() {
		return name();
	}
}