package pl.cyfronet.datanet.web.client.messages;

import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.messages.MessageDispatcher.MessageType;

import com.google.gwt.core.shared.GWT;

public class MessagePresenter {
	
	private MessageDispatcher dispatcher;
	private MessagePresenterMessages messages;

	public MessagePresenter(MessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		messages = GWT.create(MessagePresenterMessages.class);
	}
	
	public void errorNoModelPresent() {
		dispatcher.displayMessage(messages.errorNoModelPresent(), MessageType.ERROR);
	}
	
	public void errorNoRepositoryPresent() {
		dispatcher.displayMessage(messages.errorNoRepositoryPresent(), MessageType.ERROR);
	}
	
	public void displayModelSavedMessage() {
		dispatcher.displayMessage(messages.modelSaved(), MessageType.INFO);
	}
	
	public void displayModelDeployedMessage() {
		dispatcher.displayMessage(messages.modelDeployed(), MessageType.INFO);
	}
	
	public void displayRepositoryUndeployedMessage() {
		dispatcher.displayMessage(messages.repositoryUndeployed(), MessageType.INFO);
	}
	
	public void displayModelSaveError(ModelError modelError) {
		String message = messages.errorModelSavePrefix() + " ";

		switch (modelError) {
		case EMPTY_ENTITY_NAME:
			message += messages.errorEmptyEntityName();
			break;
		case NULL_MODEL:
			message += messages.errorNullModel();
			break;
		case EMPTY_FIELD_NAME:
			message += messages.errorEmptyFieldName();
			break;
		case EMPTY_MODEL_NAME:
			message += messages.errorEmptyModelName();
			break;
		case EMPTY_MODEL_VERSION:
			message += messages.errorEmptyModelVersion();
			break;
		case NULL_ENTITY_LIST:
			message += messages.errorNullEntityList();
			break;
		case NULL_FIELD_LIST:
			message += messages.errorNullFieldList();
			break;
		case NULL_FIELD_TYPE:
			message += messages.errorNullFieldType();
			break;
		}

		dispatcher.displayMessage(message, MessageType.ERROR);
	}
	
	public void displayModelDeployError(ModelError modelError) {
		String message = new String();

		switch (modelError) {
		case EMPTY_ENTITY_NAME:
			message += messages.errorEmptyEntityName();
			break;
		case NULL_MODEL:
			message += messages.errorNullModel();
			break;
		case EMPTY_FIELD_NAME:
			message += messages.errorEmptyFieldName();
			break;
		case EMPTY_MODEL_NAME:
			message += messages.errorEmptyModelName();
			break;
		case EMPTY_MODEL_VERSION:
			message += messages.errorEmptyModelVersion();
			break;
		case NULL_ENTITY_LIST:
			message += messages.errorNullEntityList();
			break;
		case NULL_FIELD_LIST:
			message += messages.errorNullFieldList();
			break;
		case NULL_FIELD_TYPE:
			message += messages.errorNullFieldType();
			break;
		}

		dispatcher.displayMessage(message, MessageType.ERROR);
	}


}
