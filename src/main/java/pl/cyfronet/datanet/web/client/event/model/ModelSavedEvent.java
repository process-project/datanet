package pl.cyfronet.datanet.web.client.event.model;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ModelSavedEvent extends GenericEvent {
	private Long modelId;

	public ModelSavedEvent(Long modelId) {
		this.modelId = modelId;
	}

	public Long getModelId() {
		return modelId;
	}
}
