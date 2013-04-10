package pl.cyfronet.datanet.web.client.messages;

import com.google.gwt.i18n.client.Messages;

public interface MessagePresenterMessages extends Messages {
	String errorNoModelPresent();
	String errorModelSavePrefix();
	String errorModelDeployPrefix();
	String errorEmptyEntityName();
	String errorNullModel();
	String errorEmptyFieldName();
	String errorEmptyModelName();
	String errorEmptyModelVersion();
	String errorNullEntityList();
	String errorNullFieldList();
	String errorNullFieldType();
	String modelSaved();
	String modelDeployed();
}
