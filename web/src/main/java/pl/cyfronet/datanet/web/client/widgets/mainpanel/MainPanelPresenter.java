package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.ClientController;

import com.google.gwt.user.client.ui.Widget;

public class MainPanelPresenter implements Presenter {
	interface View {
		void setPresenter(Presenter presenter);
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
}