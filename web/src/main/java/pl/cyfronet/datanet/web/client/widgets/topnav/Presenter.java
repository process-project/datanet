package pl.cyfronet.datanet.web.client.widgets.topnav;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	void onLogout();
	void onSwitchLocale(String string);
	void onHelp();
	IsWidget getWidget();
}
