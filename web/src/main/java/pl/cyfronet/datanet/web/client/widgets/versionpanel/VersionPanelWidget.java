package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

	private Presenter presenter;
	
	@UiField Label modelName;
	@UiField Panel entityContainer;
	@UiField Modal deployRepositoryForm;
	@UiField Button performDeploy;
	@UiField Button cancelDeploy;
	@UiField TextBox newRepositoryName;
	@UiField ControlGroup newRepositoryNameControlGroup;
	@UiField Alert newRepositoryErrorAlert;	
	
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

	@UiHandler("startDeploy")
	void onStartDeploy(ClickEvent event) {		
		cleanDeployRepositoryForm();
		deployRepositoryForm.show();
	}
	
	private void cleanDeployRepositoryForm() {
		newRepositoryErrorAlert.setVisible(false);
		newRepositoryNameControlGroup.setType(null);
		newRepositoryName.setText("");
		performDeploy.state().reset();
		cancelDeploy.setEnabled(true);
	}

	@UiHandler("performDeploy")
	void onPerformDeploy(ClickEvent event) {
		performDeploy.state().loading();
		cancelDeploy.setEnabled(false);		
		
		presenter.deploy(newRepositoryName.getText());
	}
	
	@UiHandler("cancelDeploy")
	void onCancelDeploy(ClickEvent event) {
		hideDeployModal();
	}

	@Override
	public void setDeployError(String errorMsg) {
		newRepositoryNameControlGroup.setType(ControlGroupType.ERROR);
		performDeploy.state().reset();
		cancelDeploy.setEnabled(true);
		
		newRepositoryErrorAlert.setVisible(true);
		newRepositoryErrorAlert.setText(errorMsg);
	}

	@Override
	public void hideDeployModal() {
		deployRepositoryForm.hide();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
