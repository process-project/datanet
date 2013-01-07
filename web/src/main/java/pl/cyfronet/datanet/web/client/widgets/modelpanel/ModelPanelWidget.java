package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ModelPanelWidget extends Composite implements View {
	private static ModelPanelWidgetUiBinder uiBinder = GWT.create(ModelPanelWidgetUiBinder.class);
	interface ModelPanelWidgetUiBinder extends UiBinder<Widget, ModelPanelWidget> {}
	
	private Presenter presenter;
	private ModelPanelMessages messages;
	
	@UiField TextBox modelNameTextBox;
	@UiField TextBox modelVersionTextBox;
	@UiField FlowPanel entityContainer;
	
	public ModelPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(ModelPanelMessages.class);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}	
}