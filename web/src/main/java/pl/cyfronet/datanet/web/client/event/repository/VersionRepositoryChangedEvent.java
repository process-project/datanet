package pl.cyfronet.datanet.web.client.event.repository;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class VersionRepositoryChangedEvent extends GenericEvent {
	private long repositoryId;
	private long versionId;

	public VersionRepositoryChangedEvent(long versionId, long repositoryId) {
		this.versionId = versionId;
		this.repositoryId = repositoryId;
	}
	
	public long getRepositoyId() {
		return repositoryId;
	}

	public long getVersionId() {
		return versionId;
	}
}