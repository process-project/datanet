package pl.cyfronet.datanet.web.client.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.model.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;

import com.google.gwt.user.client.Random;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class RepositoryController {

	private static final Logger logger = Logger
			.getLogger(ModelTreePanelPresenter.class.getName());

	private RepositoryServiceAsync repositoryService;
	private EventBus eventBus;
	
	private Map<Long, List<Repository>> repositories;

	private VersionController versionController;

	@Inject
	public RepositoryController(RepositoryServiceAsync repoService, VersionController versionController, EventBus eventBus) {
		this.repositoryService = repoService;
		this.versionController = versionController;
		this.eventBus = eventBus;
		this.repositories = new HashMap<Long, List<Repository>>();
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
	
	// ATTENTION !!!!!!
	// ATTENTION !!!!!! -> fake implementation :)
	// ATTENTION !!!!!!
	// TODO implement this method 
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

	public interface RepositoriesCallback {

		void setRepositories(List<Repository> list);
		
	}
}