package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ModelPanelWidget extends Composite implements View {
	private static ModelPanelWidgetUiBinder uiBinder = GWT
			.create(ModelPanelWidgetUiBinder.class);

	interface ModelPanelWidgetUiBinder extends
			UiBinder<Widget, ModelPanelWidget> {
	}

	private Presenter presenter;
	private ModelPanelMessages messages;

	@UiField
	TextBox modelName;
	@UiField
	TextBox modelVersion;
	@UiField
	Panel entityContainer;
	@UiField
	FlowPanel modelContainer;
	@UiField
	FlowPanel repositoryContainer;
	@UiField
	ModelPanelWidgetStyles style;
	
	interface ModelPanelWidgetStyles extends CssResource {
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

	public ModelPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(ModelPanelMessages.class);
	}

	@UiHandler("newEntity")
	void newEntityClicked(ClickEvent event) {
		presenter.onNewEntity();
	}

	@UiHandler("modelName")
	void modelNameChanged(ValueChangeEvent<String> event) {
		presenter.onModelNameChanged(event.getValue());
	}

	@UiHandler("modelVersion")
	void modelVersionChanged(ValueChangeEvent<String> event) {
		presenter.onModelVersionChanged(event.getValue());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasWidgets getEntityContainer() {
		return entityContainer;
	}

	@Override
	public void setModelName(String name) {
		modelName.setText(name);
	}

	@Override
	public void setModelVersion(String version) {
		modelVersion.setText(version);
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
		for (int i = 0; i < modelContainer.getWidgetCount(); i++) {
			Widget w = modelContainer.getWidget(i);
			if (w instanceof ModelLabel) {
				ModelLabel modelLabel = (ModelLabel) w;
				if (modelLabel.getModelId() == id) {
					modelContainer.remove(i);
				}
			}
		}
	}

	@Override
	public void displayNoModelsLabel() {
		modelContainer.add(new Label(messages.noModels()));
	}

	@Override
	public void markModel(long id) {
		unmarkModel();
		ModelLabel activeModel = getModelLabelByModelId(id);
		if (activeModel != null) {
			activeModel.setStyleName(style.marked(), true);
		}
	}

	@Override
	public void unmarkModel() {
		for (int i = 0; i < modelContainer.getWidgetCount(); i++) {
			modelContainer.getWidget(i).removeStyleName(style.marked());
		}
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
		repositoryContainer.add(new Label(messages.noRepositories()));
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