package pl.cyfronet.datanet.web.client.widgets.login;

import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter.View;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class LoginWidget extends Composite implements View {
	private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);
	interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {}

	@UiField Alert errorLabel;
	@UiField Button switchToPl;
	@UiField Button switchToEn;
	@UiField TextBox openIdLoginField;
	@UiField Button openIdLoginButton;
	
	private LoginMessages messages;
	private Presenter presenter;

	public LoginWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(LoginMessages.class);
	}
	
	@UiHandler("switchToPl")
	void onSwitchToPl(ClickEvent event) {
		presenter.onSwitchLocale("pl");
	}
	
	@UiHandler("switchToEn")
	void onSwitchToEn(ClickEvent event) {
		presenter.onSwitchLocale("en");
	}
	
	@UiHandler("openIdLoginButton")
	public void onOpenIdLogin(ClickEvent event) {
		presenter.onOpenIdLoginInitiated();
	}
	
	@UiHandler("openIdLoginField")
	void openIdLoginTyping(KeyUpEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			presenter.onOpenIdLoginInitiated();
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
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
	public void selectPlLocales() {
		switchToPl.setActive(true);
	}

	@Override
	public void selectEnLocales() {
		switchToEn.setActive(true);	
	}

	@Override
	public HasText getOpenIdLogin() {
		return openIdLoginField;
	}

	@Override
	public void errorOpenIdLoginEmpty() {
		errorLabel.setText(messages.openIdLoginEmpty());
		alertVisible(true);
	}

	@Override
	public void setOpenIdBusyState(boolean state) {
		if (state) {
			openIdLoginButton.state().loading();
		} else {
			openIdLoginButton.state().reset();
		}
	}

	@Override
	public void openIdLoginInitializationError() {
		errorLabel.setText(messages.openIdLoginInitializationError());
		alertVisible(true);
	}

	@Override
	public void redirect(String redirectionUrl) {
		Window.Location.replace(redirectionUrl);
	}

	@Override
	public void showExternalErrors(String id) {
		Element element = DOM.getElementById(id);
		
		if(element != null && element.getInnerText() != null && !element.getInnerText().isEmpty()) {
			errorLabel.setText(element.getInnerText());
			alertVisible(true);
		}
	}
}