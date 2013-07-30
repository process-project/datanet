package pl.cyfronet.datanet.web.client.widgets.readonly.entitypanel;

import pl.cyfronet.datanet.web.client.widgets.readonly.entitypanel.EntityPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class EntityPanelWidget extends Composite implements View {

	private static EntityPanelWidgetUiBinder uiBinder = GWT
			.create(EntityPanelWidgetUiBinder.class);

	interface EntityPanelWidgetUiBinder extends
			UiBinder<Widget, EntityPanelWidget> {
	}

	@UiField
	Label entityName;

	@UiField
	Panel fieldContainer;
	
	public EntityPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EntityPanelWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setEntityName(String name) {
		entityName.setText(name);
	}

	@Override
	public HasWidgets getFieldContainer() {
		return fieldContainer;
	}
}
