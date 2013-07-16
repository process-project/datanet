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
	
	String rpcError();
}