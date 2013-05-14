package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.messages.MessageDispatcher.MessageType;
import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelWidget extends Composite implements View {
	private static MainPanelWidgetUiBinder uiBinder = GWT.create(MainPanelWidgetUiBinder.class);
	interface MainPanelWidgetUiBinder extends UiBinder<Widget, MainPanelWidget> {}
		
	interface MainPanelWidgetStyles extends CssResource {
		String errorLabel();
		String infoLabel();
	}
	
	private Presenter presenter;
	private Timer messageLabelTimer;
	
	@UiField Panel modelsPanel;
	@UiField Panel repositoriesPanel;
	@UiField Label messageLabel;
	@UiField MainPanelWidgetStyles style;
	
	
	public MainPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
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

	public void displayMessage(String message, MessageType messageType) {
		switch(messageType) {
			case ERROR: messageLabel.setStyleName(style.errorLabel()); break;
			case INFO: messageLabel.setStyleName(style.infoLabel()); break;
		}
		
		messageLabel.setText(message);
		messageLabel.setVisible(true);
		
		if(messageLabelTimer == null) {
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

	@Override
	public void setModelBrowser(IsWidget widget) {
		modelsPanel.add(widget);
	}

	@Override
	public void setRepositoryBrowser(IsWidget widget) {
		repositoriesPanel.add(widget);
	}
}