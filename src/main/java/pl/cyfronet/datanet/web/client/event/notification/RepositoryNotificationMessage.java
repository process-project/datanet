package pl.cyfronet.datanet.web.client.event.notification;

public enum RepositoryNotificationMessage implements NotificationMessage {
	repositoryDeployed,
	repositoryNotPresent,
	repositoryLoadError,
	repositoryEntityDataLoadError,
	repositoryDeployError,
	repositorySaveEntityRowError,
	repositoryRemovalError,
	repositoryRemoved,
	repositoryMaxNumberOfRepositoriesExceeded,
	repositoryInfoRetrievalError,
	repositoryAccessConfigNotAvailable,
	repositoryAccessConfigUpdateSuccess,
	repositoryAccessConfigUpdateFailure,
	repositoryLoadErrorNoMessage,
	repositoryEntityRowRemovalFailure,
	repositoryEntityRowRemoved,
	;

	@Override
	public String getMessageCode() {
		return name();
	}
}