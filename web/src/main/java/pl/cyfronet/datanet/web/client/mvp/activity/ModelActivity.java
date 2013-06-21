package pl.cyfronet.datanet.web.client.mvp.activity;

import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.widgets.model.ModelPanel;
import pl.cyfronet.datanet.web.client.widgets.model.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.model.Presenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ModelActivity extends AbstractActivity {

	private ModelPlace place;

	@Inject
	public ModelActivity(@Assisted ModelPlace place) {
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		GWT.log("Model activity start");
		Presenter presenter = new ModelPanelPresenter(new ModelPanel(), place.getModelId());
		panel.setWidget(presenter.getWidget());
	}
}
