package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	void onNewEntity();
	void onModelNameChanged(String modelName);
	IsWidget getWidget();
	List<String> getEntitiesNames();
	Entity getEntity(String entityName);
	void onNewVersionModal();
	void onCreateNewVersion();
}
