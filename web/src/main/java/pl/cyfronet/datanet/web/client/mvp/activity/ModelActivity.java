package pl.cyfronet.datanet.web.client.mvp.activity;

import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ModelController;
import pl.cyfronet.datanet.web.client.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelWidget;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ModelActivity extends AbstractActivity {

	private static final Logger logger = Logger.getLogger(ModelActivity.class
			.getName());

	private ModelController modelController;
	private ModelPlace place;

	@Inject
	public ModelActivity(ModelController modelController,
			@Assisted ModelPlace place) {
		this.modelController = modelController;
		this.place = place;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		logger.log(Level.INFO, "Loading model with id: " + place.getModelId());
		modelController.getModel(place.getModelId(), new ModelCallback() {
			@Override
			public void setModel(Model model) {
				ModelPanelPresenter presenter = new ModelPanelPresenter(
						new ModelPanelWidget(), eventBus);
				presenter.setModel(model);
				panel.setWidget(presenter.getWidget());
			}
		});
	}
}
