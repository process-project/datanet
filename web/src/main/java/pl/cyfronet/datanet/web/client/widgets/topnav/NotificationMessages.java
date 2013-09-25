package pl.cyfronet.datanet.web.client.widgets.topnav;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface NotificationMessages extends ConstantsWithLookup {
	String modelDeployed();
	String modelNameNotUnique();
	String modelSaved();
	String modelSaveError();
	String modelDeployError();
	String modelListLoadError();
	String modelLoadError();
	String modelLoadErrorNoCause();
	String modelWrongIdFormat();
	String modelNotPresent();
	
	String repositoryDeployed();
	String repositoryNotPresent();
	String repositoryLoadError();
	String repositoryEntityDataLoadError();
	String repositoryDeployError();
	String repositorySaveEntityRowError();
	String repositoryRemovalError();
	String repositoryRemoved();
	String repositoryMaxNumberOfRepositoriesExceeded();
	String repositoryInfoRetrievalError();
	String repositoryAccessConfigNotAvailable();
	String repositoryAccessConfigUpdateSuccess();
	String repositoryAccessConfigUpdateFailure();
	
	String versionLoadError();
	String versionListLoadError();
	String versionReleased();
	String versionReleaseError();
	String versionCannotRemoveRepositoriesExist();
	String versionRemoveError();
	String versionRemoved();
	
	String rpcError();
}