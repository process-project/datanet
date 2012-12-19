package pl.cyfronet.datanet.web.client;

import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginWidget;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelWidget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
	
	private void showMainPanel() {
		MainPanelPresenter mainPanelPresenter = new MainPanelPresenter(
				new MainPanelWidget(), this);
		clearPanels();
		RootPanel.get().add(RootLayoutPanel.get());
		RootLayoutPanel.get().add(mainPanelPresenter.getWidget());
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