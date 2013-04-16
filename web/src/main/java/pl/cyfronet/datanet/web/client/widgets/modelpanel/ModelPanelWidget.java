package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ModelPanelWidget extends Composite implements View {

	private static ModelPanelWidgetUiBinder uiBinder = GWT
			.create(ModelPanelWidgetUiBinder.class);

	interface ModelPanelWidgetUiBinder extends
			UiBinder<Widget, ModelPanelWidget> {
	}

	interface ModelPanelWidgetStyles extends CssResource {
	}

	private Presenter presenter;

	@UiField
	TextBox modelName;
	@UiField
	TextBox modelVersion;
	@UiField
	Panel entityContainer;
	
	public ModelPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
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

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public HasWidgets getEntityContainer() {
		return entityContainer;
	}

	public void setModelName(String name) {
		modelName.setText(name);
	}

	public void setModelVersion(String version) {
		modelVersion.setText(version);
	}

}
