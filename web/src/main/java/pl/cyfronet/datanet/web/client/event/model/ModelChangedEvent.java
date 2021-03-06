package pl.cyfronet.datanet.web.client.event.model;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ModelChangedEvent extends GenericEvent {
	private Long modelId;

	public ModelChangedEvent(Long modelId) {
		this.modelId = modelId;
	}

	public Long getModelId() {
		return modelId;
	}
}