package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface EntityDataPanelMessages extends Messages {
	String repositorySearchLabel();
	String noEntityValues();
	String repositoryAddEntity();
	String saveNewEntityRow();
	String passwordPlaceholder();
	String loginPlaceholder();
	String password();
	String login();
	String credentialsInfo();
	String searchLimitationMessage();
	String showCodeTemplatesLabel();
	String codeTemplatesModalHeader();
	String closeCodeTemplatesModalButtonLabel();
	String codeTemplatesAbout();
	String credentialsModalLabel();
	String submitCredentialsButtonLabel();
	String cancelSubmitCredentialsButtonLabel();
	String userCredentialsAbout();
	String fieldPlaceholderPrefix();
	String actionColumnLabel();
	SafeHtml removeRowButtonLabel();
	String rowRemovalConfirmation();
}