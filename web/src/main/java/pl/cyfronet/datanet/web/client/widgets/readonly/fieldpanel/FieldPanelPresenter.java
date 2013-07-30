package pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel;

import pl.cyfronet.datanet.model.beans.Field;

import com.google.gwt.user.client.ui.IsWidget;

public class FieldPanelPresenter {

	public interface View extends IsWidget {
		void setType(String type);
		void setName(String name);
		void setRequired(boolean required);
	}

	private View view;
	
	public FieldPanelPresenter(View view) {
		this.view = view;
	}

	public void setField(Field field) {
		view.setName(field.getName());
		view.setType(String.valueOf(field.getType()));
		view.setRequired(field.isRequired());
	}

	public IsWidget getWidget() {
		return view;
	}
}
