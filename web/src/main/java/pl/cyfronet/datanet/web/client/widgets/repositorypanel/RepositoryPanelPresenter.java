package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.HashMap;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.di.factory.EntityDataPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

public class RepositoryPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void setRepositoryLink(String link);
		void addEntity(String entityName, IsWidget isWidget);
		void showEntity(int entityIndex);
		Map<String, String> getSearchFieldValues();
	}

	private View view;
	private long repositoryId;
	private RepositoryController repositoryController;
	private EntityDataPanelPresenterFactory entityDataPanelPresenterFactory;
	private Map<String, EntityDataPanelPresenter> entityDataPanelPresenters;
	
	@Inject
	public RepositoryPanelPresenter(View view, RepositoryController repositoryController,
			EntityDataPanelPresenterFactory entityDataPanelPresenterFactory) {
		this.view = view;
		this.repositoryController = repositoryController;
		this.entityDataPanelPresenterFactory = entityDataPanelPresenterFactory;
		view.setPresenter(this);
		entityDataPanelPresenters = new HashMap<String, EntityDataPanelPresenter>();
	}
	
	public void setRepository(long repositoryId) {
		this.repositoryId = repositoryId;
		repositoryController.getRepository(repositoryId, new RepositoryCallback() {
			@Override
			public void setRepository(Repository repository) {
				showRepository(repository);
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
