package pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel;

import java.util.Collections;
import java.util.List;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.messages.MessagePresenter;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

public class RepositoryBrowserPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void displayNoRepositoriesLabel();
		void clearRepositories();
		void addRepository(String repositoryName);
		void clearRepository();
		void setRepositoryPanel(IsWidget widget);
	}
	
	private View view;
	private List<String> repositories;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private ClientController clientController;
	private MessagePresenter messagePresenter;
	
	public RepositoryBrowserPanelPresenter(View view, ClientController clientController, ModelServiceAsync modelServiceAsync, RpcErrorHandler errorHandler) {
		this.view = view;
		this.clientController = clientController;
		this.messagePresenter = clientController.getMessagePresenter();
		this.modelService = modelServiceAsync;
		this.rpcErrorHandler = rpcErrorHandler;
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
			}
		} else {
			view.displayNoRepositoriesLabel();
		}
	}
		
	public void updateRepositoryList() {
		modelService.getRepositories(new AsyncCallback<List<String>>() {

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
		// TODO Auto-generated method stub
	}
	
}