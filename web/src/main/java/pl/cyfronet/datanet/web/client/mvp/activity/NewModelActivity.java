package pl.cyfronet.datanet.web.client.mvp.activity;

import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.Presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class NewModelActivity extends AbstractActivity {

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		Presenter presenter = new ModelPanelPresenter(new ModelPanelWidget(),
				eventBus);
		panel.setWidget(presenter.getWidget());
	}
}
