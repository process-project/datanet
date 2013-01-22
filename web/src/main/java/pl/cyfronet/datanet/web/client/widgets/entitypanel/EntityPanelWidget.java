package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EntityPanelWidget extends Composite implements View {
	private static EntityPanelWidgetUiBinder uiBinder = GWT.create(EntityPanelWidgetUiBinder.class);
	interface EntityPanelWidgetUiBinder extends UiBinder<Widget, EntityPanelWidget> {}

	private Presenter presenter;
	private EntityPanelMessages messages;
	
	@UiField TextBox entityName;
	@UiField Panel fieldContainer;

	public EntityPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(EntityPanelMessages.class);
	}
	
	@UiHandler("removeEntity")
	void removeEntityClicked(ClickEvent event) {
		presenter.onRemoveEntity();
	}
	
	@UiHandler("newField")
	void addFieldClicked(ClickEvent event) {
		presenter.onNewField();
	}
	
	@UiHandler("entityName")
	void entityNameChanged(ValueChangeEvent<String> event) {
		presenter.onEntityNameChanged(event.getValue());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasWidgets getFieldContainer() {
		return fieldContainer;
	}
}