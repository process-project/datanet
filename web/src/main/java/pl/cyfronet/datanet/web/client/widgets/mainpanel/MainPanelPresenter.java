package pl.cyfronet.datanet.web.client.widgets.mainpanel;

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
		void clearModels();
		void addModel(String name, String version);
		void displayNoModelsLabel();
	}

	private View view;
	private ClientController clientController;
	private ModelPanelPresenter currentModelPanelPresenter;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private List<Model> models;
	
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

	@Override
	public void onLogout() {
		clientController.onLogout();
	}

	@Override
	public void onNewModel() {
		view.getMainContainer().clear();
		currentModelPanelPresenter = new ModelPanelPresenter(new ModelPanelWidget());
		view.getMainContainer().add(currentModelPanelPresenter.getWidget().asWidget());
	}

	@Override
	public void onSaveModel() {
		if(currentModelPanelPresenter != null) {
			clientController.onSaveModel(currentModelPanelPresenter);
		} else {
			view.errorNoModelPresent();
		}
	}
	
	private void refreshModelList() {
		view.clearModels();
		
		if(models.size() > 0) {
			for(Model model : models) {
				view.addModel(model.getName(), model.getVersion());
			}
		} else {
			view.displayNoModelsLabel();
		}
	}
}