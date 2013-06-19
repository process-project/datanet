package pl.cyfronet.datanet.web.client.widgets.model;

import pl.cyfronet.datanet.web.client.widgets.model.ModelPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ModelPanel extends Composite implements View {

	private static ModelPanelUiBinder uiBinder = GWT
			.create(ModelPanelUiBinder.class);

	interface ModelPanelUiBinder extends UiBinder<Widget, ModelPanel> {
	}

	private Presenter presenter;

	@UiField
	Label label;

	public ModelPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ModelPanel(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		label.setText(title);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
