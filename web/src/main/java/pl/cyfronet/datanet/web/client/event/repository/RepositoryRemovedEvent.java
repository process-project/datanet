package pl.cyfronet.datanet.web.client.event.repository;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class RepositoryRemovedEvent extends GenericEvent {
	private long repositoryId;

	public RepositoryRemovedEvent(long repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public long getRepositoyId() {
		return repositoryId;
	}
}