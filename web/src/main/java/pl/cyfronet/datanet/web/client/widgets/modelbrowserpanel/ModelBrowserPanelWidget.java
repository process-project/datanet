package pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel;

import pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel.ModelBrowserPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ModelBrowserPanelWidget extends Composite implements View {
	private static ModelBrowserPanelWidgetUiBinder uiBinder = GWT
			.create(ModelBrowserPanelWidgetUiBinder.class);

	interface ModelBrowserPanelWidgetUiBinder extends
			UiBinder<Widget, ModelBrowserPanelWidget> {
	}

	private Presenter presenter;
	private ModelBrowserPanelMessages messages;

	@UiField
	FlowPanel modelListContainer;
	@UiField
	Panel modelContainer;
	@UiField
	ModelBrowserPanelWidgetStyles style;
	
	interface ModelBrowserPanelWidgetStyles extends CssResource {
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

	public ModelBrowserPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(ModelBrowserPanelMessages.class);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
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
		modelListContainer.clear();
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
		modelListContainer.add(modelLabel);
	}

	//TODO: probably not needed, due to using refresh and updating list in presenter
	public void removeModel(long id) {
		for (int i = 0; i < modelListContainer.getWidgetCount(); i++) {
			Widget w = modelListContainer.getWidget(i);
			if (w instanceof ModelLabel) {
				ModelLabel modelLabel = (ModelLabel) w;
				if (modelLabel.getModelId() == id) {
					modelListContainer.remove(i);
				}
			}
		}
	}

	@Override
	public void displayNoModelsLabel() {
		modelListContainer.add(new Label(messages.noModels()));
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
		for (int i = 0; i < modelListContainer.getWidgetCount(); i++) {
			modelListContainer.getWidget(i).removeStyleName(style.marked());
		}
	}

	private ModelLabel getModelLabelByModelId(long id) {
		for (int i = 0; i < modelListContainer.getWidgetCount(); i++) {
			Widget w = modelListContainer.getWidget(i);
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
	public void clearModel() {
		modelContainer.clear();
	}

	@Override
	public void setModelPanel(IsWidget widget) {
		clearModel();
		modelContainer.add(widget);
	}

}