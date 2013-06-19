package pl.cyfronet.datanet.web.client.di.provider;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

public class PlaceControllerProvider implements Provider<PlaceController> {

	private EventBus eventBus;

	@Inject
	public PlaceControllerProvider(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	@Override
	public PlaceController get() {
		return new PlaceController(eventBus);
	}
}
