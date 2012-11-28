package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.widgets.login.Presenter;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginWidget extends Composite implements View {
	private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);
	interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {}
	
	@UiField TextBox loginField;
	@UiField PasswordTextBox passwordField;
	@UiField Button loginButton;
	@UiField Label errorLabel;
	
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
	}

	@Override
	public void errorUnknownDuringLogin() {
		errorLabel.setText(messages.unknownLoginError());
	}

	@Override
	public void clearErrors() {
		errorLabel.setText("");
	}

	@Override
	public void errorWrongLoginOrPassword() {
		errorLabel.setText(messages.wrongLoginOrPassword());
	}
}