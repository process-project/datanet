package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EntityPanelWidget extends Composite implements View {
	private static EntityPanelWidgetUiBinder uiBinder = GWT.create(EntityPanelWidgetUiBinder.class);
	interface EntityPanelWidgetUiBinder extends UiBinder<Widget, EntityPanelWidget> {}

	private Presenter presenter;
	private EntityPanelMessages messages;
	
	@UiField TextBox entityName;

	public EntityPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(EntityPanelMessages.class);
	}
	
	@UiHandler("removeEntity")
	void removeEntityClicked(ClickEvent event) {
		presenter.onRemoveEntity();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}