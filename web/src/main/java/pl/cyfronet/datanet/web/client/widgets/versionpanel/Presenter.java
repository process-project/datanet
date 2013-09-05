package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	IsWidget getWidget();
	void onRemoveVersion();
	void deploy(String repositoryName);
	void onStartDeploy();
}