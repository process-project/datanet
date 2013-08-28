package pl.cyfronet.datanet.web.client.event.repository;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class RepositoryRemovedEvent extends GenericEvent {
	private long repositoryId;
	private long versionId;

	public RepositoryRemovedEvent(long versionId, long repositoryId) {
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