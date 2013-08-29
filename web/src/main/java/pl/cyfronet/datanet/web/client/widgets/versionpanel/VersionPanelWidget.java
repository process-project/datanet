package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class VersionPanelWidget extends Composite implements View {
	private static VersionPanelWidgetUiBinder uiBinder = GWT.create(VersionPanelWidgetUiBinder.class);
	interface VersionPanelWidgetUiBinder extends UiBinder<Widget, VersionPanelWidget> {}

	@UiField Label modelName;
	@UiField Panel entityContainer;
	private Provider<Presenter> presenter;
	
	@Inject
	public VersionPanelWidget(Provider<Presenter> presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("removeVersion")
	void onRemoveVersion(ClickEvent event) {
		presenter.get().onRemoveVersion();
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