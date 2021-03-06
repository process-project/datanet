package pl.cyfronet.datanet.web.client.mvp.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.web.client.di.factory.ModelPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ModelActivity extends AbstractActivity {
	private static final Logger logger = LoggerFactory.getLogger(ModelActivity.class.getName());

	private ModelController modelController;
	private Long modelId;
	private ModelPanelPresenterFactory modelPanelFactory;

	@Inject
	public ModelActivity(ModelController modelController, @Assisted Long modelId,
			ModelPanelPresenterFactory modelPanelFactory) {
		this.modelController = modelController;
		this.modelId = modelId;
		this.modelPanelFactory = modelPanelFactory;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		logger.debug("Loading model with id: {}", modelId);
		modelController.getModel(modelId, new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				ModelPanelPresenter presenter = modelPanelFactory.create();
				presenter.setModel(model);
				panel.setWidget(presenter.getWidget());
			}
		});
	}
}