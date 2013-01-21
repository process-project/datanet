package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;

import com.google.gwt.user.client.ui.IsWidget;

public class FieldPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
	}
	
	private View view;
	private Field field;
	
	public FieldPanelPresenter(EntityPanelPresenter entityPanelPresenter, FieldPanelWidget view) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public IsWidget getWidget() {
		return view;
	}
}