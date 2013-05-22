package pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel;

import java.util.Collections;
import java.util.List;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.messages.MessagePresenter;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

public class RepositoryBrowserPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void clearRepositories();
		void addRepository(String repositoryName);
		void displayNoRepositoriesLabel();
		void markRepository(String repositoryName);
		void unmarkRepository();
		void clearRepository();
		void setRepositoryPanel(IsWidget widget);
	}
	
	private View view;
	private List<String> repositories;
	private RepositoryServiceAsync repositoryService;
	private RpcErrorHandler rpcErrorHandler;
	private ClientController clientController;
	private MessagePresenter messagePresenter;
	private RepositoryPanelPresenter repositoryPanelPresenter;
	
	public RepositoryBrowserPanelPresenter(View view, ClientController clientController,
			RepositoryServiceAsync repositoryServiceAsync, RpcErrorHandler errorHandler) {
		this.view = view;
		this.clientController = clientController;
		this.messagePresenter = clientController.getMessagePresenter();
		this.repositoryService = repositoryServiceAsync;
		this.rpcErrorHandler = errorHandler;
		view.setPresenter(this);
	}

	public IsWidget getWidget() {
		return view;
	}
	
	protected void refreshRepositoryList() {
		view.clearRepositories();

		Collections.sort(repositories);
		if (repositories.size() > 0) {
			for(String repositoryName : repositories) {
				view.addRepository(repositoryName);
				
				if(repositoryPanelPresenter != null && repositoryPanelPresenter.getRepository().equals(repositoryName)) {
					view.markRepository(repositoryName);
				}
			}
		} else {
			view.displayNoRepositoriesLabel();
		}
	}
		
	public void updateRepositoryList() {
		repositoryService.getRepositories(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
			@Override
			public void onSuccess(List<String> repositories) {
				RepositoryBrowserPanelPresenter.this.repositories = repositories;
				refreshRepositoryList();
			}
		});
	}

	@Override
	public void onUndeployRepository() {
		if(repositoryPanelPresenter != null) {
			String applicationName = repositoryPanelPresenter.getRepository();
			String repositoryName = applicationName;//.substring(4);
			clientController.onUndeployRepository(repositoryName);
			view.clearRepository();
			repositoryPanelPresenter = null;
		} else {
			messagePresenter.errorNoRepositoryPresent();
		}
	}

	@Override
	public void onRepositoryClicked(String repositoryName) {
		if(repositoryPanelPresenter != null && repositoryPanelPresenter.getRepository().equals(repositoryName)) {
			return;
		}
		
		view.clearRepository();
		repositoryPanelPresenter = new RepositoryPanelPresenter(new RepositoryPanelWidget());
		repositoryPanelPresenter.setRepository(repositoryName);
		
		view.setRepositoryPanel(repositoryPanelPresenter.getWidget());
		view.markRepository(repositoryName);
	}
	
}