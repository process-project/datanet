package pl.cyfronet.datanet.web.client.controller.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter.DataCallback;

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
	
	private RepositoryServiceAsync repositoryService;
	private EventBus eventBus;
	
	@Inject
	public RepositoryController(RepositoryServiceAsync repositoryService, EventBus eventBus) {
		this.repositoryService = repositoryService;
		this.eventBus = eventBus;
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
					
					if(repository != null && repository.getSourceModel() != null &&
							repository.getSourceModel().getEntities() != null) {
						for(Entity entity : repository.getSourceModel().getEntities()) {
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

	public void getEntityRows(long repositoryId, String entityName, int start, int length, DataCallback dataCallback) {
		//TODO(DH): make the data call to the server
		if(dataCallback != null) {
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			
			for(int i = start; i < start + length; i++) {
				Map<String, String> row = new HashMap<String, String>();
				row.put("field1", "value " + i);
				result.add(row);
			}
			
			dataCallback.onData(result);
		}
	}
}