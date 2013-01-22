package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import pl.cyfronet.datanet.model.beans.Field.Type;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	IsWidget getWidget();
	void onRemoveField();
	void onFieldNameChanged(String fieldName);
	void onFieldTypeChanged(Type value);
}