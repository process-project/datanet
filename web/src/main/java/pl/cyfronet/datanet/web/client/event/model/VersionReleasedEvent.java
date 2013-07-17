package pl.cyfronet.datanet.web.client.event.model;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class VersionReleasedEvent extends GenericEvent {
	private Long modelId;
	private Long versionId;

	public VersionReleasedEvent(Long modelId, Long versionId) {
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
