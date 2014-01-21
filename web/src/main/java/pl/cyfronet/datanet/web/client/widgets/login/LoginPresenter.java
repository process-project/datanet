package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class LoginPresenter implements Presenter {
	public interface View {
		void setPresenter(Presenter presenter);
		void errorLoginOrPasswordEmpty();
		void errorUnknownDuringLogin();
		void clearErrors();
		void errorWrongLoginOrPassword();
		void selectPlLocales();
		void selectEnLocales();
		HasText getOpenIdLogin();
		void errorOpenIdLoginEmpty();
		void setOpenIdBusyState(boolean state);
		void openIdLoginInitializationError();
		void redirect(String redirectionUrl);
	}

	private ClientController clientController;
	private LoginServiceAsync loginService;
	private View view;

	public LoginPresenter(LoginServiceAsync loginService, View view,
			ClientController clientController) {
		this.clientController = clientController;
		this.loginService = loginService;
		this.view = view;
		view.setPresenter(this);
		initLocalesButtons();
	}

	private void initLocalesButtons() {
		if("pl".equals(LocaleInfo.getCurrentLocale().getLocaleName())) {
			view.selectPlLocales();
		} else {
			view.selectEnLocales();
		}
	}
	
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public void onSwitchLocale(String locale) {
		clientController.switchLocale(locale);
	}
	
	@Override
	public void onOpenIdLoginInitiated() {
		String openIdLogin = view.getOpenIdLogin().getText();
		
		if (openIdLogin.isEmpty()) {
			view.errorOpenIdLoginEmpty();
			
			return;
		}
		
		view.clearErrors();
		view.setOpenIdBusyState(true);
		loginService.initiateOpenIdLogin(openIdLogin, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				view.openIdLoginInitializationError();
				view.setOpenIdBusyState(false);
			}

			@Override
			public void onSuccess(String redirectionUrl) {
				view.setOpenIdBusyState(false);
				view.redirect(redirectionUrl);
			}
		});
	}
}