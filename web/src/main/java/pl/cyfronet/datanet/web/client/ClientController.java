package pl.cyfronet.datanet.web.client;

import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class ClientController {
	private LoginServiceAsync loginService;
	private RpcErrorHandler rpcErrorHandler;
	
	public ClientController(LoginServiceAsync loginService, RpcErrorHandler rpcErrorHandler) {
		this.loginService = loginService;
		this.rpcErrorHandler = rpcErrorHandler;
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
	
	private void showMainPanel() {
		MainPanelPresenter mainPanelPresenter = new MainPanelPresenter();
		RootPanel.get().clear();
		RootPanel.get().add(mainPanelPresenter.getWidget());
	}

	private void showLoginPanel() {
		LoginPresenter loginPresenter = new LoginPresenter(loginService, rpcErrorHandler,
				new LoginWidget());
		RootPanel.get().clear();
		RootPanel.get().add(loginPresenter.getWidget());
	}
}