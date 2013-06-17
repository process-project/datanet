package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.ClientController;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelPresenter implements Presenter {
	interface View {
		void setPresenter(Presenter presenter);
		void setModelBrowser(IsWidget widget);
		void setRepositoryBrowser(IsWidget widget);
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
	@Override
	public void onSwitchLocale(String locale) {
		clientController.switchLocale(locale);
	}
	@Override
	public void onHelp() {
		String path = Window.Location.getPath();
		
		if(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		
		Window.open(Window.Location.createUrlBuilder().setPath(path + "/help").buildString(), "_blank", "");
	}
}