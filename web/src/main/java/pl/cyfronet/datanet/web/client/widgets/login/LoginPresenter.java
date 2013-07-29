package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class LoginPresenter implements Presenter {
	public interface View {
		void setPresenter(Presenter presenter);
		HasText getLogin();
		HasText getPassword();
		void errorLoginOrPasswordEmpty();
		void errorUnknownDuringLogin();
		void clearErrors();
		void errorWrongLoginOrPassword();
		void setBusyState(boolean busy);
	}

	private ClientController clientController;
	private LoginServiceAsync loginService;
	private View view;
	private EventBus eventBus;

	public LoginPresenter(LoginServiceAsync loginService, View view,
			ClientController clientController, EventBus eventBus) {
		this.clientController = clientController;
		this.loginService = loginService;
		this.eventBus = eventBus;
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

		if (login.isEmpty() || password.isEmpty()) {
			view.errorLoginOrPasswordEmpty();

			return;
		}

		view.clearErrors();
		view.setBusyState(true);
		loginService.login(login, password, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				view.setBusyState(false);
				clientController.onLogin();
			}

			@Override
			public void onFailure(Throwable t) {
				view.setBusyState(false);
				
				if (t instanceof LoginException) {
					LoginException e = (LoginException) t;

					switch (e.getErrorCode()) {
					case Unknown:
						view.errorUnknownDuringLogin();
						break;
					case UserPasswordUnknown:
						view.errorWrongLoginOrPassword();
						break;
					}
				} else {
					view.errorUnknownDuringLogin();
				}
			}
		});
	}

	@Override
	public void onSwitchLocale(String locale) {
		clientController.switchLocale(locale);
	}
}