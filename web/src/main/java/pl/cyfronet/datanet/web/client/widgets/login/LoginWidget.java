package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter.View;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class LoginWidget extends Composite implements View {
	private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);
	interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {}
	
	@UiField TextBox loginField;
	@UiField PasswordTextBox passwordField;
	@UiField Alert errorLabel;
	@UiField Button loginButton;
	@UiField Button switchToPl;
	@UiField Button switchToEn;
	
	private LoginMessages messages;
	private Presenter presenter;

	public LoginWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(LoginMessages.class);
	}
	
	@UiHandler("loginButton")
	void loginClicked(ClickEvent event) {
		presenter.onLogin();
	}
	@UiHandler("loginField")
	void loginTyping(KeyUpEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			presenter.onLogin();
		}
	}
	@UiHandler("passwordField")
	void passwordTyping(KeyUpEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			presenter.onLogin();
		}
	}
	@UiHandler("switchToPl")
	void onSwitchToPl(ClickEvent event) {
		presenter.onSwitchLocale("pl");
	}
	@UiHandler("switchToEn")
	void onSwitchToEn(ClickEvent event) {
		presenter.onSwitchLocale("en");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasText getLogin() {
		return loginField;
	}

	@Override
	public HasText getPassword() {
		return passwordField;
	}

	@Override
	public void errorLoginOrPasswordEmpty() {
		errorLabel.setText(messages.loginOrPasswordEmpty());
		alertVisible(true);
	}
	
	@Override
	public void errorUnknownDuringLogin() {
		errorLabel.setText(messages.unknownLoginError());
		alertVisible(true);
	}
	
	@Override
	public void clearErrors() {
		errorLabel.setText("");
		alertVisible(false);
	}
	
	@Override
	public void errorWrongLoginOrPassword() {
		errorLabel.setText(messages.wrongLoginOrPassword());
		alertVisible(true);
	}
	
	private void alertVisible(boolean visible) {
		errorLabel.setVisible(visible);
	}

	@Override
	public void setBusyState(boolean busy) {
		if (busy) {
			loginButton.setIcon(IconType.SPINNER);
			loginButton.setEnabled(false);
		} else {
			loginButton.setIcon(IconType.SIGNIN);
			loginButton.setEnabled(true);
		}
	}
	
	@Override
	public void selectPlLocales() {
		switchToPl.setActive(true);
	}

	@Override
	public void selectEnLocales() {
		switchToEn.setActive(true);	
	}
}