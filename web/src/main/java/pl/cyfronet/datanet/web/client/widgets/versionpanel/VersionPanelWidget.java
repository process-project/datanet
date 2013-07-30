package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class VersionPanelWidget extends Composite implements View {

	private static VersionPanelWidgetUiBinder uiBinder = GWT
			.create(VersionPanelWidgetUiBinder.class);

	interface VersionPanelWidgetUiBinder extends
			UiBinder<Widget, VersionPanelWidget> {
	}

	@UiField Label modelName;
	@UiField Panel entityContainer;
	
	public VersionPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public VersionPanelWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setModelName(String name) {
		modelName.setText(name);
	}

	@Override
	public HasWidgets getEntityContainer() {
		return entityContainer;
	}

}
