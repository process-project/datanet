package pl.cyfronet.datanet.web.client;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.messages.MessagePresenter;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel.ModelBrowserPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel.ModelBrowserPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class ClientController {
	private LoginServiceAsync loginService;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private ModelValidator modelValidator;
	private MainPanelPresenter mainPanelPresenter;
	private ModelBrowserPanelPresenter modelBrowserPanelPresenter;
	private MessagePresenter messagePresenter;
	
	public ClientController(LoginServiceAsync loginService, ModelServiceAsync modelService,
			RpcErrorHandler rpcErrorHandler, ModelValidator modelValidator) {
		this.loginService = loginService;
		this.modelService = modelService;
		this.rpcErrorHandler = rpcErrorHandler;
		this.modelValidator = modelValidator;
	}
	
	public void start() {
		loginService.isUserLoggedIn(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean isLoggedIn) {
				if(!isLoggedIn) {
					showLoginPanel();
				} else {
					showMainPanel();
				}
			}
			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
		});
	}
	
	public void onLogin() {
		showMainPanel();
	}
	
	public void onLogout() {
		loginService.logout(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
			@Override
			public void onSuccess(Void v) {
				showLoginPanel();
			}
		});
	}
	
	
	public void onSaveModel(final Model model) {
		List<ModelError> modelErrors = modelValidator.validateModel(model);
		
		if(modelErrors.isEmpty()) {
			modelService.saveModel(model, new AsyncCallback<Model>() {
				@Override
				public void onFailure(Throwable t) {
					rpcErrorHandler.handleRpcError(t);
				}
				@Override
				public void onSuccess(Model m) {
					messagePresenter.displayModelSavedMessage();
					
					modelBrowserPanelPresenter.addOrReplaceModel(m);
					modelBrowserPanelPresenter.setMarked(m.getId());
					modelBrowserPanelPresenter.onModelClicked(m.getId());
				}
			});
		} else {
			messagePresenter.displayModelSaveError(modelErrors.get(0));
		}
	}
	
	public MessagePresenter getMessagePresenter() {
		return messagePresenter;
	}
	
	public void onDeployModel(final Model model) {
		//TODO: deployment needs proper validation and error handling, this was copied from onSaveModel()
		List<ModelError> modelErrors = modelValidator.validateModel(model);
		
		if(modelErrors.isEmpty()) {
			modelService.deployModel(model, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable t) {
					rpcErrorHandler.handleRpcError(t);
				}
				@Override
				public void onSuccess(Void v) {
					messagePresenter.displayModelDeployedMessage();
					modelBrowserPanelPresenter.updateRepositoryList();
				}
			});
		} else {
			messagePresenter.displayModelDeployError(modelErrors.get(0));
		}
	}
	
	private void showMainPanel() {
		modelBrowserPanelPresenter = new ModelBrowserPanelPresenter(
				new ModelBrowserPanelWidget(), this, modelService, rpcErrorHandler
				);
		
		MainPanelWidget mainPanelWidget = new MainPanelWidget();
		mainPanelWidget.setModelBrowser(modelBrowserPanelPresenter.getWidget());
		mainPanelPresenter = new MainPanelPresenter(
				mainPanelWidget, this);
		
		messagePresenter = new MessagePresenter(mainPanelPresenter);
		
		clearPanels();
		RootPanel.get().add(RootLayoutPanel.get());
		RootLayoutPanel.get().add(mainPanelPresenter.getWidget());
		modelBrowserPanelPresenter.updateModelList();
		modelBrowserPanelPresenter.updateRepositoryList();
	}

	private void showLoginPanel() {
		LoginPresenter loginPresenter = new LoginPresenter(loginService, rpcErrorHandler,
				new LoginWidget(), this);
		clearPanels();
		RootPanel.get().add(loginPresenter.getWidget());
	}
	
	private void clearPanels() {
		if(RootLayoutPanel.get().getWidgetCount() > 0) {
			RootLayoutPanel.get().clear();
		}
		
		if(RootPanel.get().getWidgetCount() > 0) {
			RootPanel.get().clear();
		}
	}
}