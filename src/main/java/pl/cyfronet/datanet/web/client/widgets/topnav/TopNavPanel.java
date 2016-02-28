package pl.cyfronet.datanet.web.client.widgets.topnav;

import pl.cyfronet.datanet.web.client.util.CsrfUtil;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter.View;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;

public class TopNavPanel extends Composite implements View {
	private static TopNavPanelUiBinder uiBinder = GWT.create(TopNavPanelUiBinder.class);
	interface TopNavPanelUiBinder extends UiBinder<Widget, TopNavPanel> {}
	
	public enum MessageType {
		ERROR,
		INFO
	}
	
	private Timer messageLabelTimer;
	private Presenter presenter;

	@UiField Alert messageLabel;
	@UiField Button switchToPl;
	@UiField Button switchToEn;
	@UiField FormPanel downloadProxyForm;
	
	public TopNavPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		downloadProxyForm.add(new Hidden(CsrfUtil.getCsrfParameterName(), CsrfUtil.getCsrfValue()));
	}
	
	public void displayMessage(String message, MessageType messageType) {
		switch (messageType) {
		case ERROR:
			messageLabel.setType(AlertType.ERROR);
			break;
		case INFO:
			messageLabel.setType(AlertType.INFO);
			break;
		}

		messageLabel.setText(message);
		messageLabel.setVisible(true);

		if (messageLabelTimer == null) {
			messageLabelTimer = new Timer() {
				@Override
				public void run() {
					messageLabel.setVisible(false);
					messageLabel.setText("");
					messageLabelTimer = null;
				}
			};
		} else {
			messageLabelTimer.cancel();
		}

		messageLabelTimer.schedule(2000);
	}

	@UiHandler("logout")
	void logoutClicked(ClickEvent event) {
		presenter.onLogout();
	}

	@UiHandler("switchToPl")
	void onSwitchToPl(ClickEvent event) {
		presenter.onSwitchLocale("pl");
	}

	@UiHandler("switchToEn")
	void onSwitchToEn(ClickEvent event) {
		presenter.onSwitchLocale("en");
	}

	@UiHandler("help")
	void onHelp(ClickEvent event) {
		presenter.onHelp();
	}
	
	@UiHandler("retrieveProxy")
	void retrieveProxy(ClickEvent event) {
		presenter.onRetrieveProxy();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
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
	public void submitProxyForm() {
		downloadProxyForm.submit();
	}
}