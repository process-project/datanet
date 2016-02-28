package pl.cyfronet.datanet.web.client.widgets.readonly.entitypanel;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel.FieldPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel.FieldPanelWidget;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class EntityPanelPresenter {

	public interface View extends IsWidget {
		void setEntityName(String name);

		HasWidgets getFieldContainer();
	}

	private View view;

	public EntityPanelPresenter(View view) {
		this.view = view;
	}

	public void setEntity(Entity entity) {
		view.setEntityName(entity.getName());
		view.getFieldContainer().clear();

		for (Field field : entity.getFields()) {
			FieldPanelPresenter fieldPanelPresenter = new FieldPanelPresenter(
					new FieldPanelWidget());
			fieldPanelPresenter.setField(field);
			view.getFieldContainer().add(
					fieldPanelPresenter.getWidget().asWidget());
		}
	}

	public IsWidget getWidget() {
		return view;
	}
}
