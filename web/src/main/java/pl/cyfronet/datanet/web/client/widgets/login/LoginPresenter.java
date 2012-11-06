package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class LoginPresenter implements Presenter {
	interface View {
		void setPresenter(Presenter presenter);
		HasText getLogin();
		HasText getPassword();
		void errorLoginOrPasswordEmpty();
		void errorUnknownDuringLogin();
		void clearErrors();
	}
	
	private LoginServiceAsync loginService;
	private RpcErrorHandler rpcErrorHandler;
	private View view;

	public LoginPresenter(LoginServiceAsync loginService,
			RpcErrorHandler rpcErrorHandler, View view) {
		this.loginService = loginService;
		this.rpcErrorHandler = rpcErrorHandler;
		this.view = view;
		view.setPresenter(this);
	}

	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public void onLogin() {
		String login = view.getLogin().getText();
		String password = view.getPassword().getText();
		
		if(login.isEmpty() || password.isEmpty()) {
			view.errorLoginOrPasswordEmpty();
			
			return;
		}
		
		view.clearErrors();
		loginService.login(login, password, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				//notify controller
			}
			
			@Override
			public void onFailure(Throwable t) {
				if(t instanceof LoginException) {
					LoginException e = (LoginException) t;
					switch(e.getErrorCode()) {
						case Unknown:
							view.errorUnknownDuringLogin();
						break;
					}
					
				} else {
					rpcErrorHandler.handleRpcError(t);
				}
			}
		});
	}
}