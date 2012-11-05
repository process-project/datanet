package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LoginPresenter {
	private LoginServiceAsync loginService;
	private RpcErrorHandler rpcErrorHandler;

	public LoginPresenter(LoginServiceAsync loginService,
			RpcErrorHandler rpcErrorHandler) {
		this.loginService = loginService;
		this.rpcErrorHandler = rpcErrorHandler;
	}

	public Widget getWidget() {
		return new Label("Login widget");
	}

}