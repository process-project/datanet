package pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.messages.MessagePresenter;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

public class ModelBrowserPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void clearModels();
		void addModel(long id, String name, String version);
		void removeModel(long id);
		void displayNoModelsLabel();
		void markModel(long id);
		void unmarkModel();
		void displayNoRepositoriesLabel();
		void clearRepositories();
		void addRepository(String repositoryName);
		void clearModel();
		void setModelPanel(IsWidget widget);
	}
	
	private View view;
	private List<Model> models;
	private List<String> repositories;
	private ModelPanelPresenter modelPanelPresenter;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private ClientController clientController;
	private MessagePresenter messagePresenter;
	
	public ModelBrowserPanelPresenter(View view, ClientController clientController, ModelServiceAsync modelServiceAsync, RpcErrorHandler errorHandler) {
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
	
	@Override
	public void onNewModel() {
		view.clearModel();
		view.unmarkModel();
		modelPanelPresenter = new ModelPanelPresenter(new ModelPanelWidget());
		view.setModelPanel(modelPanelPresenter.getWidget());
	}

	@Override
	public void onSaveModel() {
		if(modelPanelPresenter != null) {
			clientController.onSaveModel(modelPanelPresenter.getModel());
		} else {
			messagePresenter.errorNoModelPresent();
		}
	}
	
	@Override
	public void onDeployModel() {
		if(modelPanelPresenter != null) {
			clientController.onDeployModel(modelPanelPresenter.getModel());
		} else {
			messagePresenter.errorNoModelPresent();
		}
	}
	
	@Override
	public void onModelClicked(long id) {
		if(modelPanelPresenter != null && modelPanelPresenter.getModel().getId() == id) {
			return;
		}
		
		view.clearModel();
		modelPanelPresenter = new ModelPanelPresenter(new ModelPanelWidget());
		Model model = getModelById(id);
		modelPanelPresenter.setModel(model);

		view.setModelPanel(modelPanelPresenter.getWidget());
		view.markModel(model.getId());
	}
	
	private void refreshModelList() {
		view.clearModels();
		
		if(models.size() > 0) {
			Collections.sort(models, new Comparator<Model>() {
				
				@Override
				public int compare(Model o1, Model o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				
			});
			for(Model model : models) {
				view.addModel(model.getId(), model.getName(), model.getVersion());
				
				//mark active model
				if (modelPanelPresenter != null && modelPanelPresenter.getModel().getId() == model.getId()) {
					view.markModel(modelPanelPresenter.getModel().getId());
				}
			}
		} else {
			view.displayNoModelsLabel();
		}
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
		
	private Model getModelById(Long id) {
		for (Model m : models) {
			if(m.getId() == id) {
				return m;
			}
		}
		return null;
	}
	
	public void setMarked(long id) {
		view.markModel(id);
	}
	
	public void addOrReplaceModel(Model model) {
		Model old = null;
		for (Model m : models) {
			if (m.getId() == model.getId()) {
				old = m;
				break;
			}
		}
		if (old == null) {
			models.add(model);
		} else {
			models.remove(old);
			models.add(model);
		}
		refreshModelList();
	}
	
	public void updateModelList() {
		modelService.getModels(new AsyncCallback<List<Model>>() {
			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
			@Override
			public void onSuccess(List<Model> models) {
				ModelBrowserPanelPresenter.this.models = models;
				refreshModelList();
			}});
	}
	
	public void updateRepositoryList() {
		modelService.getRepositories(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
			@Override
			public void onSuccess(List<String> repositories) {
				ModelBrowserPanelPresenter.this.repositories = repositories;
				refreshRepositoryList();
			}
		});
	}
	
}