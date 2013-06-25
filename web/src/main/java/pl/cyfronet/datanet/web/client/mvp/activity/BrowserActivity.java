package pl.cyfronet.datanet.web.client.mvp.activity;

import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.NewModelPlace;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class BrowserActivity extends AbstractActivity {

	private ModelTreePanelPresenter modelTreePanelPresenter;

	@Inject
	public BrowserActivity(ModelTreePanelPresenter modelTreePanelPresenter) {
		this.modelTreePanelPresenter = modelTreePanelPresenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(modelTreePanelPresenter.getWidget());
		modelTreePanelPresenter.reload();
	}

	public void setPlace(ModelPlace place) {
		modelTreePanelPresenter.setSelected(place.getModelId(), ItemType.MODEL);
	}

	public void setPlace(NewModelPlace place) {

	}
}
