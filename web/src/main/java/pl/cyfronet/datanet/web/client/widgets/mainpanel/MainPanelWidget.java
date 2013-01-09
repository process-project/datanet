package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelWidget extends Composite implements View {
	private static MainPanelWidgetUiBinder uiBinder = GWT.create(MainPanelWidgetUiBinder.class);
	interface MainPanelWidgetUiBinder extends UiBinder<Widget, MainPanelWidget> {}

	private MainPanelMessages messages;
	private Presenter presenter;
	private Timer errorLabelTimer;
	
	@UiField Panel mainContainer;
	@UiField Label errorLabel;
	
	public MainPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(MainPanelMessages.class);
	}
	
	@UiHandler("logout")
	void logoutClicked(ClickEvent event) {
		presenter.onLogout();
	}
	
	@UiHandler("newModel")
	void newModelClicked(ClickEvent event) {
		presenter.onNewModel();
	}
	
	@UiHandler("saveModel")
	void saveModelClicked(ClickEvent event) {
		presenter.onSaveModel();
	}

	@Override
	public HasWidgets getMainContainer() {
		return mainContainer;
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void errorNoModelPresent() {
		errorLabel.setText(messages.errorNoModelPresent());
		errorLabel.setVisible(true);
		
		if(errorLabelTimer == null) {
			errorLabelTimer = new Timer() {
				@Override
				public void run() {
					errorLabel.setVisible(false);
					errorLabel.setText("");
					errorLabelTimer = null;
				}
			};
		} else {
			errorLabelTimer.cancel();
		}
		
		errorLabelTimer.schedule(2000);
	}
}