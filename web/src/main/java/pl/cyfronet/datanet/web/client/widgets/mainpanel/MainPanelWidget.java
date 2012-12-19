package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelWidget extends Composite implements View {
	private static MainPanelWidgetUiBinder uiBinder = GWT.create(MainPanelWidgetUiBinder.class);
	interface MainPanelWidgetUiBinder extends UiBinder<Widget, MainPanelWidget> {}

	private MainPanelMessages messages;
	private Presenter presenter;
	
	public MainPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(MainPanelMessages.class);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("logout")
	void logoutClicked(ClickEvent event) {
		presenter.onLogout();
	}
}