package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import com.google.gwt.i18n.client.Messages;

public interface MainPanelMessages extends Messages {
	String logoutLabel();
	String datanetMain();
	String modelsLabel();
	String repositoriesLabel();
	String newModelLabel();
	String saveModelLabel();
	String deployModelLabel();
	String errorNoModelPresent();
	String errorModelSavePrefix();
	String errorEmptyEntityName();
	String errorNullModel();
	String errorEmptyFieldName();
	String errorEmptyModelName();
	String errorEmptyModelVersion();
	String errorNullEntityList();
	String errorNullFieldList();
	String errorNullFieldType();
	String modelSavedMessage();
	String modelDeployedMessage();
	String noModelsLabel();
}