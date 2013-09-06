package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class VersionPanelWidget extends Composite implements View {
	private static VersionPanelWidgetUiBinder uiBinder = GWT.create(VersionPanelWidgetUiBinder.class);
	interface VersionPanelWidgetUiBinder extends UiBinder<Widget, VersionPanelWidget> {}
	
	@UiField Label versionName;
	@UiField Panel entityContainer;
	@UiField Modal deployRepositoryForm;
	@UiField Button performDeploy;
	@UiField Button cancelDeploy;
	@UiField TextBox newRepositoryName;
	@UiField ControlGroup newRepositoryNameControlGroup;
	@UiField Alert newRepositoryErrorAlert;
	@UiField Button removeVersion;
	@UiField Button startDeploy;

	private Presenter presenter;
	private VersionPanelWidgetMessages messages;	

	@Inject
	public VersionPanelWidget(VersionPanelWidgetMessages messages) {
		this.messages = messages;
		initWidget(uiBinder.createAndBindUi(this));
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
	
	@UiHandler("removeVersion")
	void onRemoveVersion(ClickEvent event) {
		presenter.onRemoveVersion();
	}
	
	@UiHandler("startDeploy")
	void onStartDeploy(ClickEvent event) {		
		presenter.onStartDeploy();
	}

	@Override
	public void setModelName(String name) {
		versionName.setText(name);
	}

	@Override
	public HasWidgets getEntityContainer() {
		return entityContainer;
	}
	
	private void cleanDeployRepositoryForm() {
		newRepositoryErrorAlert.setVisible(false);
		newRepositoryNameControlGroup.setType(null);
		newRepositoryName.setText("");
		performDeploy.state().reset();
		cancelDeploy.setEnabled(true);
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

	@Override
	public boolean confirmVersionRemoval() {
		return Window.confirm(messages.versionRemovalConfirmation());
	}

	@Override
	public void setRemoveVersionBusyState(boolean busy) {
		if (busy) {
			removeVersion.setIcon(IconType.SPINNER);
			removeVersion.setEnabled(false);
		} else {
			removeVersion.setIcon(IconType.REMOVE);
			removeVersion.setEnabled(true);
		}
	}

	@Override
	public void showDeployRepositoryModal() {
		cleanDeployRepositoryForm();
		deployRepositoryForm.show();
	}

	@Override
	public void setStartDeployBusyState(boolean state) {
		if (state) {
			startDeploy.setEnabled(false);
			startDeploy.setIcon(IconType.SPINNER);
		} else {
			startDeploy.setEnabled(true);
			startDeploy.setIcon(IconType.CLOUD_UPLOAD);
		}
	}
}