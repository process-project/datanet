package pl.cyfronet.datanet.web.client.controller.repository;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class RepositoryController {
	public interface RepositoryCallback {
		void setRepository(Repository repository);
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
}