package pl.cyfronet.datanet.web.client.event.model;

import com.google.gwt.event.shared.EventHandler;

public interface ModelChangedEventHandler extends EventHandler {
	void onModelChangedEvent(ModelChangedEvent event);
}
