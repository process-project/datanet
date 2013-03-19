package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelPresenter implements Presenter {
	interface View {
		void setPresenter(Presenter presenter);
		HasWidgets getMainContainer();
		void errorNoModelPresent();
		void displayModelSaveError(ModelError modelError);
		void displayModelSavedMessage();
		void displayModelDeployError(ModelError modelError);
		void displayModelDeployedMessage();
		void clearModels();
		void addModel(long id, String name, String version);
		void removeModel(long id);
		void displayNoModelsLabel();
		void markModel(long id);
		void unmarkModel();
		void displayNoRepositoriesLabel();
		void clearRepositories();
		void addRepository(String repositoryName);
	}

	private View view;
	private ClientController clientController;
	private ModelPanelPresenter currentModelPanelPresenter;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private List<Model> models;
	private List<String> repositories;
	
	public MainPanelPresenter(View view, ClientController clientController,
			ModelServiceAsync modelService, RpcErrorHandler rpcErrorHandler) {
		this.view = view;
		this.clientController = clientController;
		this.modelService = modelService;
		this.rpcErrorHandler = rpcErrorHandler;
		view.setPresenter(this);
	}
	
	public Widget getWidget() {
		return (Widget) view;
	}
	
	public void displayModelSaveError(ModelError modelError) {
		view.displayModelSaveError(modelError);		
	}
	
	public void displayModelSavedInfo() {
		view.displayModelSavedMessage();
	}
	
	public void displayModelDeployedInfo() {
		view.displayModelDeployedMessage();
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
				MainPanelPresenter.this.models = models;
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
				MainPanelPresenter.this.repositories = repositories;
				refreshRepositoryList();
			}
		});
	}

	@Override
	public void onLogout() {
		clientController.onLogout();
	}

	@Override
	public void onNewModel() {
		view.getMainContainer().clear();
		currentModelPanelPresenter = new ModelPanelPresenter(new ModelPanelWidget());
		view.getMainContainer().add(currentModelPanelPresenter.getWidget().asWidget());
		
		view.unmarkModel();
	}

	@Override
	public void onSaveModel() {
		if(currentModelPanelPresenter != null) {
			clientController.onSaveModel(currentModelPanelPresenter);
		} else {
			view.errorNoModelPresent();
		}
	}
	
	@Override
	public void onDeployModel() {
		if(currentModelPanelPresenter != null) {
			clientController.onDeployModel(currentModelPanelPresenter.getModel());
		} else {
			view.errorNoModelPresent();
		}
	}
	
	@Override
	public void onModelClicked(long id) {
		if(currentModelPanelPresenter != null && currentModelPanelPresenter.getModel().getId() == id) {
			return;
		}
		
		view.getMainContainer().clear();
		currentModelPanelPresenter = new ModelPanelPresenter(new ModelPanelWidget());
		currentModelPanelPresenter.setModel(getModelById(id));
		view.getMainContainer().add(currentModelPanelPresenter.getWidget().asWidget());

		view.markModel(id);
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
				if (currentModelPanelPresenter != null && currentModelPanelPresenter.getModel().getId() == model.getId()) {
					view.markModel(currentModelPanelPresenter.getModel().getId());
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

	public void displayModelDeployError(ModelError modelError) {
		view.displayModelDeployError(modelError);
	}
}