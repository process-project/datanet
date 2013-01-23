package pl.cyfronet.datanet.web.client;

import java.util.List;

import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class ClientController {
	private LoginServiceAsync loginService;
	private ModelServiceAsync modelService;
	private RpcErrorHandler rpcErrorHandler;
	private ModelValidator modelValidator;
	private MainPanelPresenter mainPanelPresenter;
	
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
	
	public void onSaveModel(ModelPanelPresenter modelPanelPresenter, final Command command) {
		List<ModelError> modelErrors = modelValidator.validateModel(modelPanelPresenter.getModel());
		
		if(modelErrors.isEmpty()) {
			modelService.saveModel(modelPanelPresenter.getModel(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable t) {
					rpcErrorHandler.handleRpcError(t);
				}
				@Override
				public void onSuccess(Void v) {
					mainPanelPresenter.displayModelSavedInfo();
					
					if(command != null) {
						command.execute();
					}
				}
			});
		} else {
			mainPanelPresenter.displayModelSaveError(modelErrors.get(0));
		}
	}
	
	private void showMainPanel() {
		mainPanelPresenter = new MainPanelPresenter(
				new MainPanelWidget(), this, modelService, rpcErrorHandler);
		clearPanels();
		RootPanel.get().add(RootLayoutPanel.get());
		RootLayoutPanel.get().add(mainPanelPresenter.getWidget());
		mainPanelPresenter.updateModelList();
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