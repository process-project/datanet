package pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

public class RepositoryBrowserPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void clearRepositories();
		void addRepository(Repository repository);
		void displayNoRepositoriesLabel();
		void markRepository(long id);
		void unmarkRepository();
		void clearRepository();
		void setRepositoryPanel(IsWidget widget);
	}
	
	private View view;
	private List<Repository> repositories;
	private RepositoryServiceAsync repositoryService;
	private RpcErrorHandler rpcErrorHandler;
	private ClientController clientController;
	private RepositoryPanelPresenter repositoryPanelPresenter;
	private EventBus eventBus;
	
	public RepositoryBrowserPanelPresenter(View view, ClientController clientController,
			RepositoryServiceAsync repositoryServiceAsync, RpcErrorHandler errorHandler,
			EventBus eventBus) {
		this.view = view;
		this.clientController = clientController;
		this.repositoryService = repositoryServiceAsync;
		this.rpcErrorHandler = errorHandler;
		this.eventBus = eventBus;
		view.setPresenter(this);
	}

	public IsWidget getWidget() {
		return view;
	}
	
	protected void refreshRepositoryList() {
		view.clearRepositories();

		Collections.sort(repositories, new Comparator<Repository>() {

			@Override
			public int compare(Repository o1, Repository o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
			
		});
		if (repositories.size() > 0) {
			for(Repository repository : repositories) {
				view.addRepository(repository);
				
				if(repositoryPanelPresenter != null && repositoryPanelPresenter.getRepository().getId() == repository.getId()) {
					view.markRepository(repository.getId());
				}
			}
		} else {
			view.displayNoRepositoriesLabel();
		}
	}
		
	public void updateRepositoryList() {
		repositoryService.getRepositories(new AsyncCallback<List<Repository>>() {

			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
			@Override
			public void onSuccess(List<Repository> repositories) {
				RepositoryBrowserPanelPresenter.this.repositories = repositories;
				refreshRepositoryList();
			}
		});
	}

	@Override
	public void onUndeployRepository() {
		if(repositoryPanelPresenter != null) {
			Repository repository = repositoryPanelPresenter.getRepository();
			clientController.onUndeployRepository(repository.getId());
			view.clearRepository();
			repositoryPanelPresenter = null;
		} else {
			eventBus.fireEvent(new NotificationEvent(RepositoryNotificationMessage.repositoryNotPresent, NotificationType.ERROR));
		}
	}

	@Override
	public void onRepositoryClicked(long repositoryId) {
		if(repositoryPanelPresenter != null && repositoryPanelPresenter.getRepository().getId() == repositoryId) {
			return;
		}
		
		Repository repo = getRepositoryById(repositoryId);
		if(repo == null) {
			//clicked repository not in list?
			return;
		}
		
		view.clearRepository();
		repositoryPanelPresenter = new RepositoryPanelPresenter(new RepositoryPanelWidget());
		repositoryPanelPresenter.setRepository(repo);
		
		view.setRepositoryPanel(repositoryPanelPresenter.getWidget());
		view.markRepository(repo.getId());
	}
	
	private Repository getRepositoryById(long repositoryId) {
		Repository repo = null;
		for(Repository repository : repositories) {
			if (repository.getId() == repositoryId) {
				repo = repository;
			}
		}
		return repo;
	}
	
}
