package pl.cyfronet.datanet.web.client.event.model;

import com.google.gwt.event.shared.EventHandler;

public interface NewModelEventHandler extends EventHandler {
	void onNewModelEvent(NewModelEvent event);
}
