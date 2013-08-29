package pl.cyfronet.datanet.web.client.event.notification;

public enum VersionNotificationMessage implements NotificationMessage {
	versionLoadError,
	versionListLoadError,
	versionReleased, 
	versionReleaseError,
	versionCannotRemoveRepositoriesExist,
	versionRemoveError,
	versionRemoved,
	;

	@Override
	public String getMessageCode() {
		return name();
	}
}