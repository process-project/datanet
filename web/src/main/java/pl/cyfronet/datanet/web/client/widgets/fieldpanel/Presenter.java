package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	IsWidget getWidget();
	void onRemoveField();
	void onFieldNameChanged(String fieldName);
	void onFieldTypeChanged(String typeName);
	void onFieldRequiredChanged(boolean value);
}