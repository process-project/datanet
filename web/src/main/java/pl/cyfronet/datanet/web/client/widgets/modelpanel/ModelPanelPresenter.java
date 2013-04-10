package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.messages.MessagePresenter;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class ModelPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getEntityContainer();
		void setModelName(String name);
		void setModelVersion(String version);
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
	private List<EntityPanelPresenter> entityPanelPresenters;
	private Model model;
	private List<Model> models;
	private List<String> repositories;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private ClientController clientController;
	private MessagePresenter messagePresenter;
	
	public ModelPanelPresenter(View view, ClientController clientController, ModelServiceAsync modelServiceAsync, RpcErrorHandler errorHandler) {
		this.view = view;
		this.clientController = clientController;
		this.messagePresenter = clientController.getMessagePresenter();
		this.modelService = modelServiceAsync;
		this.rpcErrorHandler = rpcErrorHandler;
		view.setPresenter(this);
		entityPanelPresenters = new ArrayList<EntityPanelPresenter>();
		model = new Model();
	}

	public IsWidget getWidget() {
		return view;
	}
	
	public void removeEntity(EntityPanelPresenter entityPanelPresenter) {
		view.getEntityContainer().remove(entityPanelPresenter.getWidget().asWidget());
		entityPanelPresenters.remove(entityPanelPresenter);
		model.getEntities().remove(entityPanelPresenter.getEntity());
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void onNewEntity() {
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(this, new EntityPanelWidget());
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
		model.getEntities().add(entityPanelPresenter.getEntity());
	}

	@Override
	public void onModelNameChanged(String modelName) {
		model.setName(modelName);
	}

	@Override
	public void onModelVersionChanged(String versionName) {
		model.setVersion(versionName);
	}
	
	public void setModel(Model model) {
		this.model = model;
		view.setModelName(model.getName());
		view.setModelVersion(model.getVersion());
		view.getEntityContainer().clear();
		entityPanelPresenters.clear();
		
		for(Entity entity : model.getEntities()) {
			displayEntity(entity);
		}
	}

	private void displayEntity(Entity entity) {
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(this, new EntityPanelWidget());
		entityPanelPresenter.setEntity(entity);
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
	}
	
	@Override
	public void onNewModel() {
		view.getEntityContainer().clear();
		model = new Model();
		
		view.unmarkModel();
	}

	@Override
	public void onSaveModel() {
		if(model != null) {
			clientController.onSaveModel(this);
		} else {
			messagePresenter.errorNoModelPresent();
		}
	}
	
	@Override
	public void onDeployModel() {
		if(model != null) {
			clientController.onDeployModel(this.getModel());
		} else {
			messagePresenter.errorNoModelPresent();
		}
	}
	
	@Override
	public void onModelClicked(long id) {
		if(model == null || model.getId() == id) {
			return;
		}
		
		view.getEntityContainer().clear();
		model = getModelById(id);

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
				if (model != null && this.model.getId() == model.getId()) {
					view.markModel(this.model.getId());
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
		messagePresenter.displayModelDeployError(modelError);
	}
	
	public void displayModelSaveError(ModelError modelError) {
		messagePresenter.displayModelSaveError(modelError);		
	}
	
	public void displayModelDeployedInfo() {
		messagePresenter.displayModelDeployedMessage();
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
				ModelPanelPresenter.this.models = models;
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
				ModelPanelPresenter.this.repositories = repositories;
				refreshRepositoryList();
			}
		});
	}
	
}