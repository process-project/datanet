package pl.cyfronet.datanet.web.client.event.model;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ModelDeletedEvent extends GenericEvent {

	private Long modelId;

	public ModelDeletedEvent(Long modelId) {
		this.modelId = modelId;
	}

	public Long getModelId() {
		return modelId;
	}
}
