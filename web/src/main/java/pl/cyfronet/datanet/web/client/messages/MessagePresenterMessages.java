package pl.cyfronet.datanet.web.client.messages;

import com.google.gwt.i18n.client.Messages;

public interface MessagePresenterMessages extends Messages {
	String errorNoModelPresent();
	String errorNoRepositoryPresent();
	String errorModelSavePrefix();
	String errorModelDeployPrefix();
	String errorEmptyEntityName();
	String errorNullModel();
	String errorEmptyFieldName();
	String errorEmptyModelName();
	String errorInvalidCharsInModelName();
	String errorModelNameNotUnique();
	String errorEmptyModelVersion();
	String errorNullEntityList();
	String errorNullFieldList();
	String errorNullFieldType();
	String modelSaved();
	String modelDeployed();
	String repositoryUndeployed();
}
