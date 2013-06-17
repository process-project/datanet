package pl.cyfronet.datanet.web.client.widgets.topnav;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.messages.MessageDispatcher;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class TopNavPresenter implements Presenter, MessageDispatcher {

	interface View {
		void setPresenter(Presenter presenter);

		void displayMessage(String message, MessageType type);
	}

	private ClientController clientController;
	private View view;

	public TopNavPresenter(View view, ClientController clientController) {
		this.view = view;
		this.clientController = clientController;
		view.setPresenter(this);
	}

	@Override
	public void onLogout() {
		clientController.onLogout();
	}

	@Override
	public void onSwitchLocale(String locale) {
		clientController.switchLocale(locale);
	}

	@Override
	public void onHelp() {
		String path = Window.Location.getPath();

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		Window.open(Window.Location.createUrlBuilder().setPath(path + "/help")
				.buildString(), "_blank", "");
	}

	@Override
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public void displayMessage(String message, MessageType type) {
		view.displayMessage(message, type);
	}
}
