package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	void onNewEntity();
	void onModelNameChanged(String modelName);
	void onModelVersionChanged(String versionName);
	IsWidget getWidget();
}
