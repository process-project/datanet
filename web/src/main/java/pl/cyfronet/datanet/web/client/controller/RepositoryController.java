package pl.cyfronet.datanet.web.client.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter.DataCallback;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class RepositoryController {
	public interface RepositoryCallback {
		void setRepository(Repository repository);
	}
	
	public interface EntityCallback {
		void setEntity(Entity entity);
	}
	
	public interface RepositoriesCallback {
		void setRepositories(List<Repository> list);
	}
	
	private RepositoryServiceAsync repositoryService;
	private EventBus eventBus;
	private HashMap<Long, List<Repository>> repositories;
	private VersionController versionController;
	
	
	@Inject
	public RepositoryController(RepositoryServiceAsync repositoryService, EventBus eventBus,
			VersionController versionController) {
		this.repositoryService = repositoryService;
		this.eventBus = eventBus;
		this.versionController = versionController;
		repositories = new HashMap<Long, List<Repository>>();
	}

	public void getRepository(long repositoryId, final RepositoryCallback repositoryCallback) {
		repositoryService.getRepository(repositoryId, new AsyncCallback<Repository>() {
			@Override
			public void onSuccess(Repository repository) {
				if(repositoryCallback != null) {
					repositoryCallback.setRepository(repository);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new NotificationEvent(RepositoryNotificationMessage.repositoryLoadError,
						NotificationType.ERROR, caught.getMessage()));
			}
		});
	}
	
	public void getEntity(long repositoryId, final String entityName, final EntityCallback entityCallback) {
		repositoryService.getRepository(repositoryId, new AsyncCallback<Repository>() {
			@Override
			public void onSuccess(Repository repository) {
				if(entityCallback != null) {
					Entity result = null;
					
					if(repository != null && repository.getSourceModelVersion() != null &&
							repository.getSourceModelVersion().getEntities() != null) {
						for(Entity entity : repository.getSourceModelVersion().getEntities()) {
							if(entity != null && entity.getName().equals(entityName)) {
								result = entity;
								break;
							}
						}
					}
					
					entityCallback.setEntity(result);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new NotificationEvent(RepositoryNotificationMessage.repositoryLoadError,
						NotificationType.ERROR, caught.getMessage()));
			}
		});
	}

	public void getEntityRows(long repositoryId, final String entityName, int start, int length, final DataCallback dataCallback) {
		repositoryService.getData(repositoryId, entityName, start, length, new AsyncCallback<List<Map<String, String>>>() {
			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new NotificationEvent(
						RepositoryNotificationMessage.repositoryEntityDataLoadError, NotificationType.ERROR, entityName));
			}
			@Override
			public void onSuccess(List<Map<String, String>> result) {
				if (dataCallback != null) {
					dataCallback.onData(result);
				}
			}});
	}
	
	public void getRepositories(long versionId, final RepositoriesCallback callback, boolean forceRefresh) {
		if (repositories.get(versionId) == null || forceRefresh)
			loadRepositories(versionId, callback);
		else
			callback.setRepositories(repositories.get(versionId));
	}
	
	private void loadRepositories(long versionId, RepositoriesCallback callback) {
		fakeRepositories(versionId, callback);
	}
	
	void fakeRepositories(final Long versionId, final RepositoriesCallback callback) {
		versionController.getVersion(versionId, new VersionCallback() {
			@Override
			public void setVersion(Version version) {
				final Repository repository = new Repository();
				repository.setName("repo1");
				repository.setId(Random.nextInt(100000));
				repository.setSourceModelVersion(version);
				LinkedList<Repository> linkedList = new LinkedList<Repository>();
				linkedList.add(repository);
				callback.setRepositories(linkedList);
			}
		});
	}
}