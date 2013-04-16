package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.messages.MessageDispatcher;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelPresenter implements Presenter, MessageDispatcher {
	interface View {
		void setPresenter(Presenter presenter);
		void setModelBrowser(IsWidget widget);
		void setRepositoryBrowser(IsWidget widget);
		void displayMessage(String message, MessageType type);
	}

	private View view;
	private ClientController clientController;
	
	public MainPanelPresenter(View view, ClientController clientController) {
		this.view = view;
		this.clientController = clientController;
		view.setPresenter(this);
	}
	
	public Widget getWidget() {
		return (Widget) view;
	}
	
	@Override
	public void onLogout() {
		clientController.onLogout();
	}
	
	public void displayMessage(String message, MessageType type) {
		view.displayMessage(message, type);
	}
}