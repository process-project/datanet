package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelWidget extends Composite implements View {
	private static MainPanelWidgetUiBinder uiBinder = GWT.create(MainPanelWidgetUiBinder.class);
	interface MainPanelWidgetUiBinder extends UiBinder<Widget, MainPanelWidget> {}
	
	enum MessageType {
		INFO,
		ERROR
	}
	
	interface MainPanelWidgetStyles extends CssResource {
		String errorLabel();
		String infoLabel();
		String modelLabel();
		String marked();
	}
	
	private class ModelLabel extends Label {
		private long modelId;

		public long getModelId() {
			return modelId;
		}

		public void setModelId(long modelId) {
			this.modelId = modelId;
		}
	}

	private MainPanelMessages messages;
	private Presenter presenter;
	private Timer messageLabelTimer;
	
	@UiField Panel mainContainer;
	@UiField Label messageLabel;
	@UiField MainPanelWidgetStyles style;
	@UiField FlowPanel modelContainer;
	@UiField FlowPanel repositoryContainer;
	
	public MainPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(MainPanelMessages.class);
	}
	
	@UiHandler("logout")
	void logoutClicked(ClickEvent event) {
		presenter.onLogout();
	}
	
	@UiHandler("newModel")
	void newModelClicked(ClickEvent event) {
		presenter.onNewModel();
	}
	
	@UiHandler("saveModel")
	void saveModelClicked(ClickEvent event) {
		presenter.onSaveModel();
	}
	
	@UiHandler("deployModel")
	void deployModelClicked(ClickEvent event) {
		presenter.onDeployModel();
	}

	@Override
	public HasWidgets getMainContainer() {
		return mainContainer;
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void errorNoModelPresent() {
		displayMessage(messages.errorNoModelPresent(), MessageType.ERROR);
	}

	@Override
	public void displayModelSaveError(ModelError modelError) {
		String message = messages.errorModelSavePrefix() + " ";
		
		switch(modelError) {
			case EMPTY_ENTITY_NAME: message += messages.errorEmptyEntityName(); break;
			case NULL_MODEL: message += messages.errorNullModel(); break;
			case EMPTY_FIELD_NAME: message += messages.errorEmptyFieldName(); break;
			case EMPTY_MODEL_NAME: message += messages.errorEmptyModelName(); break;
			case EMPTY_MODEL_VERSION: message += messages.errorEmptyModelVersion(); break;
			case NULL_ENTITY_LIST: message += messages.errorNullEntityList(); break;
			case NULL_FIELD_LIST: message += messages.errorNullFieldList(); break;
			case NULL_FIELD_TYPE: message += messages.errorNullFieldType(); break;
		}
		
		displayMessage(message, MessageType.ERROR);
	}
	
	@Override
	public void displayModelSavedMessage() {
		displayMessage(messages.modelSavedMessage(), MessageType.INFO);
	}
	
	@Override
	public void displayModelDeployError(ModelError modelError) {
		String message = new String();
		
		switch(modelError) {
			case EMPTY_ENTITY_NAME: message += messages.errorEmptyEntityName(); break;
			case NULL_MODEL: message += messages.errorNullModel(); break;
			case EMPTY_FIELD_NAME: message += messages.errorEmptyFieldName(); break;
			case EMPTY_MODEL_NAME: message += messages.errorEmptyModelName(); break;
			case EMPTY_MODEL_VERSION: message += messages.errorEmptyModelVersion(); break;
			case NULL_ENTITY_LIST: message += messages.errorNullEntityList(); break;
			case NULL_FIELD_LIST: message += messages.errorNullFieldList(); break;
			case NULL_FIELD_TYPE: message += messages.errorNullFieldType(); break;
		}
		
		displayMessage(message, MessageType.ERROR);
	}
	
	@Override
	public void displayModelDeployedMessage() {
		displayMessage(messages.modelDeployedMessage(), MessageType.INFO);
	}
	
	@Override
	public void clearModels() {
		modelContainer.clear();
	}

	@Override
	public void addModel(final long id, String name, String version) {
		ModelLabel modelLabel = new ModelLabel();
		modelLabel.setModelId(id);
		modelLabel.setText(name + "(" + version + ")");
		modelLabel.setStyleName(style.modelLabel(), true);
		modelLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onModelClicked(id);
			}
		});
		modelContainer.add(modelLabel);
	}
	
	public void removeModel(long id) {
		for(int i = 0; i<modelContainer.getWidgetCount(); i++) {
			Widget w = modelContainer.getWidget(i);
			if (w instanceof ModelLabel) {
				ModelLabel modelLabel = (ModelLabel) w;
				if(modelLabel.getModelId() == id) {
					modelContainer.remove(i);
				}
			}
		}
	}
	
	@Override
	public void displayNoModelsLabel() {
		modelContainer.add(new Label(messages.noModelsLabel()));
	}
	
	@Override
	public void markModel(long id) {
		unmarkModel();
		ModelLabel activeModel = getModelLabelByModelId(id);
		if(activeModel != null) {
			activeModel.setStyleName(style.marked(), true);
		}
	}
	
	@Override
	public void unmarkModel() {
		for (int i = 0; i < modelContainer.getWidgetCount(); i++) {
			modelContainer.getWidget(i).removeStyleName(style.marked());
		}
	}

	private void displayMessage(String message, MessageType messageType) {
		switch(messageType) {
			case ERROR: messageLabel.setStyleName(style.errorLabel()); break;
			case INFO: messageLabel.setStyleName(style.infoLabel()); break;
		}
		
		messageLabel.setText(message);
		messageLabel.setVisible(true);
		
		if(messageLabelTimer == null) {
			messageLabelTimer = new Timer() {
				@Override
				public void run() {
					messageLabel.setVisible(false);
					messageLabel.setText("");
					messageLabelTimer = null;
				}
			};
		} else {
			messageLabelTimer.cancel();
		}
		
		messageLabelTimer.schedule(2000);
	}
	
	private ModelLabel getModelLabelByModelId(long id) {
		for (int i = 0; i < modelContainer.getWidgetCount(); i++) {
			Widget w = modelContainer.getWidget(i);
			if (w instanceof ModelLabel) {
				ModelLabel modelLabel = (ModelLabel) w;
				if (modelLabel.getModelId() == id) {
					return modelLabel;
				}
			}
		}
		return null;
	}

	@Override
	public void displayNoRepositoriesLabel() {
		repositoryContainer.add(new Label(messages.noRepositoriesLabel()));
	}

	@Override
	public void clearRepositories() {
		repositoryContainer.clear();
	}

	@Override
	public void addRepository(String repositoryName) {
		repositoryContainer.add(new Label(repositoryName));
	}
}