package pl.cyfronet.datanet.web.client.widgets.login;

import com.google.gwt.i18n.client.Messages;

public interface LoginMessages extends Messages {
	String loginHeader();
	String loginLabel();
	String passwordLabel();
	String submitLabel();
	String loginOrPasswordEmpty();
	String unknownLoginError();
}