package pl.cyfronet.datanet.web.client.event.version;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ModelVersionChangedEvent extends GenericEvent {
	private Long modelId;
	private Long versionId;

	public ModelVersionChangedEvent(Long modelId, Long versionId) {
		this.modelId = modelId;
		this.versionId = versionId;
	}

	public Long getModelId() {
		return modelId;
	}

	public Long getVersionId() {
		return versionId;
	}
}