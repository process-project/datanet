package pl.cyfronet.datanet.web.client.mvp.activity;

import pl.cyfronet.datanet.web.client.widgets.welcome.WelcomePanel;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class WelcomeActivity extends AbstractActivity {

	public WelcomeActivity() {

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		GWT.log("Welcome activity start");
		panel.setWidget(new WelcomePanel());
	}
}
