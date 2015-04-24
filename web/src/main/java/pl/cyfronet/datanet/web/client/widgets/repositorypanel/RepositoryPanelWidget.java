package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryPanelWidget extends ResizeComposite implements View {
	private static RepositoryPanelWidgetUiBinder uiBinder = GWT.create(RepositoryPanelWidgetUiBinder.class);
	interface RepositoryPanelWidgetUiBinder extends UiBinder<Widget, RepositoryPanelWidget> {}
	interface RepositoryPanelWidgetStyles extends CssResource {}

	private Presenter presenter;

	@UiField Anchor repositoryLink;
	@UiField TabPanel tabPanel;
	@UiField RepositoryPanelMessages messages;
	@UiField Modal accessConfigModal;
	@UiField RadioButton privateCheck;
	@UiField RadioButton publicCheck;
	@UiField TextBox owners;
	@UiField TextBox corsOrigins;
	@UiField Button saveAccessConfig;
	@UiField Button accessConfiguration;
	@UiField Button removeRepository;
	@UiField FlowPanel errorPanel;
	@UiField RadioButton isolationEnabled;
	@UiField RadioButton isolationDisabled;
	
	public RepositoryPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("removeRepository")
	void removeRepository(ClickEvent event) {
		presenter.onRemoveRepository();
	}
	
	@UiHandler("accessConfiguration")
	void onShowAccessConfigModal(ClickEvent event) {
		presenter.onShowAccessConfig();
	}
	
	@UiHandler("cancelAccessConfig")
	void onCancelAccessConfig(ClickEvent event) {
		accessConfigModal.hide();
	}
	
	@UiHandler("saveAccessConfig")
	void onSaveAccessConfig(ClickEvent event) {
		presenter.onSaveAccessConfig();
	}
	
	@UiHandler("privateCheck")
	void onPrivateTypeSelected(ClickEvent event) {
		presenter.privateTypeSelected();
	}
	
	@UiHandler("publicCheck")
	void onPublicTypeSelected(ClickEvent event) {
		presenter.publicTypeSelected();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setRepositoryLink(String link) {
		repositoryLink.setEnabled(true);
		repositoryLink.setHref(link);
		repositoryLink.setText(link);
	}

	@Override
	public void addEntity(final String entityName, IsWidget widget) {
		Tab tab = new Tab();
		tab.setHeading(entityName);
		tab.add(widget.asWidget());
		tabPanel.add(tab);
	}

	@Override
	public void showEntity(int entityIndex) {
		tabPanel.selectTab(entityIndex);
	}

	@Override
	public boolean confirmRepositoryRemoval() {
		return Window.confirm(messages.repositoryRemovalConfirmation());
	}

	@Override
	public String getAccessLevel() {
		if(privateCheck.getValue()) {
			return "privateAccess";
		} else {
			return "publicAccess";
		}
	}

	@Override
	public HasText getOwnerList() {
		return owners;
	}

	@Override
	public HasText getCorsOrigins() {
		return corsOrigins;
	}
	
	@Override
	public void markPublicType() {
		privateCheck.setValue(false);
		publicCheck.setValue(true);
	}

	@Override
	public void markPrivateType() {
		publicCheck.setValue(false);
		privateCheck.setValue(true);
	}

	@Override
	public void showAccessConfigModal(boolean show) {
		if (show) {
			accessConfigModal.show();
		} else {
			accessConfigModal.hide();
		}
	}

	@Override
	public void enableOwnersInput(boolean enabled) {
		owners.setEnabled(enabled);
	}

	@Override
	public void setAccessConfigSaveBusyState(boolean state) {
		if (state) {
			saveAccessConfig.state().loading();
		} else {
			saveAccessConfig.state().reset();
		}
	}

	@Override
	public void showMissingRepositoryErrorMessage() {
		errorPanel.add(new Label(LabelType.IMPORTANT, messages.missingRepositoryError()));
	}

	@Override
	public void enableControls(boolean enabled) {
		accessConfiguration.setEnabled(enabled);
		removeRepository.setEnabled(enabled);
	}

	@Override
	public void setMissingRepositoryLink() {
		repositoryLink.setText(messages.missingRepositoryLink());
	}

	@Override
	public String getIsolation() {
		if(isolationEnabled.getValue()) {
			return "isolationEnabled";
		} else {
			return "isolationDisabled";
		}
	}

	@Override
	public void markIsolation(boolean isolation) {
		isolationEnabled.setValue(isolation);
		isolationDisabled.setValue(!isolation);
	}
}