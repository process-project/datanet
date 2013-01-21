package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FieldPanelWidget extends Composite implements View {
	private static FieldPanelWidgetUiBinder uiBinder = GWT	.create(FieldPanelWidgetUiBinder.class);
	interface FieldPanelWidgetUiBinder extends UiBinder<Widget, FieldPanelWidget> {}
	
	private Presenter presenter;

	public FieldPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}