package pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel;

import pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel.FieldPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FieldPanelWidget extends Composite implements View {
	private static FieldPanelWidgetUiBinder uiBinder = GWT.create(FieldPanelWidgetUiBinder.class);
	interface FieldPanelWidgetUiBinder extends UiBinder<Widget, FieldPanelWidget> {}

	@UiField Label name;
	@UiField Label type;
	@UiField Label required;
	
	public FieldPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public FieldPanelWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setType(String fieldType) {
		type.setText(fieldType);
	}

	@Override
	public void setName(String fieldName) {
		name.setText(fieldName);
	}

	@Override
	public void setRequired(boolean fieldRequired) {
		required.setVisible(fieldRequired);
	}
}
