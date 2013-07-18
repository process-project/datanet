package pl.cyfronet.datanet.web.client.event.notification;

public enum ModelNotificationMessage implements NotificationMessage {
	modelNameNotUnique,
	modelSaved,
	modelSaveError,
	modelDeployed,
	modelDeployError,
	modelListLoadError,
	modelLoadError,
	modelLoadErrorNoCause,
	modelWrongIdFormat,
	modelNotPresent,
	modelNotSaved,
	versionLoadError,
	versionListLoadError,
	versionReleased, 
	versionReleaseError
	;

	@Override
	public String getMessageCode() {
		return name();
	}
}