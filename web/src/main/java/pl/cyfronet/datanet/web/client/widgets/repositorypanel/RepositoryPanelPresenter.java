package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.HashMap;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.di.factory.EntityDataPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;
import pl.cyfronet.datanet.web.client.event.repository.VersionRepositoryChangedEvent;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class RepositoryPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void setRepositoryLink(String link);
		void addEntity(String entityName, IsWidget isWidget);
		void showEntity(int entityIndex);
		Map<String, String> getSearchFieldValues();
		boolean confirmRepositoryRemoval();
	}

	private View view;
	private long repositoryId;
	private RepositoryController repositoryController;
	private EntityDataPanelPresenterFactory entityDataPanelPresenterFactory;
	private Map<String, EntityDataPanelPresenter> entityDataPanelPresenters;
	private PlaceController placeController;
	private EventBus eventBus;
	
	@Inject
	public RepositoryPanelPresenter(View view, RepositoryController repositoryController,
			EntityDataPanelPresenterFactory entityDataPanelPresenterFactory,
			PlaceController placeController, EventBus eventBus) {
		this.view = view;
		this.repositoryController = repositoryController;
		this.entityDataPanelPresenterFactory = entityDataPanelPresenterFactory;
		this.placeController = placeController;
		this.eventBus = eventBus;
		view.setPresenter(this);
		entityDataPanelPresenters = new HashMap<String, EntityDataPanelPresenter>();
	}
	
	@Override
	public void onRemoveRepository() {
		if (view.confirmRepositoryRemoval()) {
			repositoryController.getRepository(repositoryId, new RepositoryCallback() {
				@Override
				public void setRepository(Repository repository) {
					final long versionId = repository.getSourceModelVersion().getId();
					repositoryController.removeRepository(versionId, repositoryId,
						new Command() {
							@Override
							public void execute() {
								eventBus.fireEvent(new VersionRepositoryChangedEvent(versionId, repositoryId));
								placeController.goTo(new VersionPlace(versionId));
								eventBus.fireEvent(new NotificationEvent(
										RepositoryNotificationMessage.repositoryRemoved, NotificationType.SUCCESS));
							}
					}, new Command() {
							@Override
							public void execute() {
								eventBus.fireEvent(new NotificationEvent(
										RepositoryNotificationMessage.repositoryRemovalError, NotificationType.ERROR));
							}});
				}

				@Override
				public void setError(String message) {
				}
			});
		}
	}
	
	public void setRepository(long repositoryId) {
		this.repositoryId = repositoryId;
		repositoryController.getRepository(repositoryId, new RepositoryCallback() {
			@Override
			public void setRepository(Repository repository) {
				showRepository(repository);
			}

			@Override
			public void setError(String message) {
			}});
	}

	public IsWidget getWidget() {
		return view;
	}
	
	private void showRepository(Repository repository) {
		view.setRepositoryLink(repository.getUrl());
		
		if(repository.getSourceModelVersion() != null && repository.getSourceModelVersion().getEntities() != null) {
			for(Entity entity : repository.getSourceModelVersion().getEntities()) {
				entityDataPanelPresenters.put(entity.getName(),
						entityDataPanelPresenterFactory.create(repositoryId, entity.getName()));
				view.addEntity(entity.getName(), entityDataPanelPresenters.get(entity.getName()).getWidget());
				entityDataPanelPresenters.get(entity.getName()).show();
			}
			
			if(repository.getSourceModelVersion().getEntities().size() > 0) {
				view.showEntity(0);
			}
		}
	}
}