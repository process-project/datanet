package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;

public interface Presenter {
	void onRemoveEntity();
	void onNewField();
	void onEntityNameChanged(String entityName);
	List<String> getEntitiesNames();
	Entity getEntity(String entityName);
}