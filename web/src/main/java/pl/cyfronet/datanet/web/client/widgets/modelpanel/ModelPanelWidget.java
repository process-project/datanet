package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ModelPanelWidget extends Composite implements View {
	private static ModelPanelWidgetUiBinder uiBinder = GWT.create(ModelPanelWidgetUiBinder.class);
	interface ModelPanelWidgetUiBinder extends UiBinder<Widget, ModelPanelWidget> {}

	private Presenter presenter;
	private ModelPanelMessages messages;
	
	@UiField TextBox modelName;
	@UiField Panel entityContainer;
	@UiField Modal newVersionModal;
	@UiField TextBox newVersionName;
	@UiField ControlGroup newVersionControls;
	@UiField Button newVersionConfirm;
	@UiField Button newVersionModalClose;	
	@UiField Button remove;
	
	@Inject
	public ModelPanelWidget(ModelPanelMessages messages) {
		initWidget(uiBinder.createAndBindUi(this));
		this.messages = messages;
	}

	@UiHandler("newEntity")
	void newEntityClicked(ClickEvent event) {
		presenter.onNewEntity();
	}

	@UiHandler("modelName")
	void modelNameChanged(ValueChangeEvent<String> event) {
		presenter.onModelNameChanged(event.getValue());
	}
	
	@UiHandler("newVersion")
	void onNewVersion(ClickEvent event) {
		presenter.onNewVersionModal();
	}
	
	@UiHandler("newVersionConfirm")
	void onNewVersionConfirm(ClickEvent event) {
		presenter.onCreateNewVersion();
	}
	
	@UiHandler("newVersionModalClose")
	void onNewVersionModalClose(ClickEvent event) {
		newVersionModal.hide();
	}
	
	@UiHandler("remove")
	void onRemove(ClickEvent event) {
		remove.state().loading();
		presenter.onRemove();
	}	
	
	@Override
	public void resetRemoveButton() {
		remove.state().reset();
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

	@Override
	public void showNewVersionModal() {
		newVersionModal.show();
	}

	@Override
	public HasText getNewVersionText() {
		return newVersionName;
	}

	@Override
	public void setNewVersionErrorState(boolean state) {
		if (state) {
			newVersionControls.setType(ControlGroupType.ERROR);
		} else {
			newVersionControls.setType(ControlGroupType.NONE);
		}
	}

	@Override
	public void setNewVersionBusyState(boolean busy) {
		if (busy) {
			newVersionConfirm.setIcon(IconType.SPINNER);
			newVersionConfirm.setEnabled(false);
			newVersionModalClose.setEnabled(false);
		} else {
			newVersionConfirm.setIcon(IconType.BRIEFCASE);
			newVersionConfirm.setEnabled(true);
			newVersionModalClose.setEnabled(true);
		}
	}

	@Override
	public void hideNewVersionModal() {
		newVersionModal.hide();
	}

	@Override
	public boolean confirmModelRemoval() {
		return Window.confirm(messages.confirmModelRemoval());
	}	
}