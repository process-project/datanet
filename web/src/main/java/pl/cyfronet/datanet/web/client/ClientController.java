package pl.cyfronet.datanet.web.client;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.messages.MessagePresenter;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.services.RepositoryServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel.ModelBrowserPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel.ModelBrowserPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel.RepositoryBrowserPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel.RepositoryBrowserPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPanel;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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
	private RepositoryBrowserPanelPresenter repositoryBrowserPanelPresenter;
	private MessagePresenter messagePresenter;
	private RepositoryServiceAsync repositoryService;
	private AppProperties properties;
	
	public ClientController(LoginServiceAsync loginService, ModelServiceAsync modelService,
			RepositoryServiceAsync repositoryService, RpcErrorHandler rpcErrorHandler,
			ModelValidator modelValidator) {
		this.loginService = loginService;
		this.modelService = modelService;
		this.rpcErrorHandler = rpcErrorHandler;
		this.modelValidator = modelValidator;
		this.repositoryService = repositoryService;
		this.properties = properties;
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
	
	private void saveModel(final Model model, final Command onSuccess) {
		modelService.saveModel(model, new AsyncCallback<Model>() {
			@Override
			public void onFailure(Throwable t) {
				//TODO: redesign exception handling
				if(t instanceof ModelException) {
					ModelException modelException = (ModelException) t;
					if(modelException.getErrorCode() == Code.ModelNameNotUnique) {
						//show name-not-unique message
						messagePresenter.errorModelNameNotUnique();
					}
				} else {
					rpcErrorHandler.handleRpcError(t);
				}
			}
			@Override
			public void onSuccess(Model m) {
				if(onSuccess != null) {
					onSuccess.execute();
				}
				
				messagePresenter.displayModelSavedMessage();
				
				modelBrowserPanelPresenter.addOrReplaceModel(m);
				modelBrowserPanelPresenter.setMarked(m.getId());
				modelBrowserPanelPresenter.onModelClicked(m.getId());
			}
		});
	}
	
	public void onSaveModel(Model model) {
		List<ModelError> modelErrors = modelValidator.validateModel(model);

		if(modelErrors.isEmpty()) {
			saveModel(model, null);
		} else {
			messagePresenter.displayModelSaveError(modelErrors.get(0));
		}
	}
	
	public MessagePresenter getMessagePresenter() {
		return messagePresenter;
	}
	
	private void deployModel(final Model model) {
		repositoryService.deployModel(model, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}
			@Override
			public void onSuccess(Void v) {
				messagePresenter.displayModelDeployedMessage();
				repositoryBrowserPanelPresenter.updateRepositoryList();
			}
		});
	}
	
	public void onDeployModel(final Model model) {
		//TODO: deployment needs proper validation and error handling, this was copied from onSaveModel()
		List<ModelError> modelErrors = modelValidator.validateModel(model);

		if(modelErrors.isEmpty()) {
			saveModel(model, new Command() {
				@Override
				public void execute() {
					deployModel(model);
				}
			});
		} else {
			messagePresenter.displayModelDeployError(modelErrors.get(0));
		}
	}

	public void switchLocale(String locale) {
		UrlBuilder builder = Window.Location.createUrlBuilder();
		builder.setParameter("locale", locale);
		Window.Location.assign(builder.buildString());
	}
	
	public void onUndeployRepository(long repositoryId) {
		repositoryService.undeployRepository(repositoryId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable t) {
				rpcErrorHandler.handleRpcError(t);
			}

			@Override
			public void onSuccess(Void result) {
				messagePresenter.displayRepositoryUndeployedMessage();
				repositoryBrowserPanelPresenter.updateRepositoryList();
			}
			
		});
	}
	
	private void showMainPanel() {
		MainPanelWidget mainPanelWidget = new MainPanelWidget();
		mainPanelPresenter = new MainPanelPresenter(
				mainPanelWidget, this);
		
		TopNavPresenter topNavPresenter = new TopNavPresenter(new TopNavPanel(), this);
		
		messagePresenter = new MessagePresenter(topNavPresenter);
		
		modelBrowserPanelPresenter = new ModelBrowserPanelPresenter(
				new ModelBrowserPanelWidget(), this, modelService, rpcErrorHandler
				);
		
		repositoryBrowserPanelPresenter = new RepositoryBrowserPanelPresenter(
				new RepositoryBrowserPanelWidget(), this, repositoryService, rpcErrorHandler
				);		
		
		mainPanelWidget.setTopNav(topNavPresenter.getWidget());
		mainPanelWidget.setModelBrowser(modelBrowserPanelPresenter.getWidget());
		mainPanelWidget.setRepositoryBrowser(repositoryBrowserPanelPresenter.getWidget());
		
		clearPanels();
		RootPanel.get().add(RootLayoutPanel.get());
		RootLayoutPanel.get().add(mainPanelPresenter.getWidget());
		modelBrowserPanelPresenter.updateModelList();
		repositoryBrowserPanelPresenter.updateRepositoryList();
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