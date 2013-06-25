package pl.cyfronet.datanet.web.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.event.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ModelController {

	private static final Logger logger = Logger
			.getLogger(ModelTreePanelPresenter.class.getName());

	private ModelServiceAsync modelService;
	private EventBus eventBus;

	@Inject
	public ModelController(ModelServiceAsync modelService, EventBus eventBus) {
		this.modelService = modelService;
		this.eventBus = eventBus;
	}

	public void loadModels(final ModelsCallback callback) {
		logger.log(Level.INFO, "Loading user models");
		modelService.getModels(new AsyncCallback<List<Model>>() {
			@Override
			public void onSuccess(List<Model> result) {
				logger.log(Level.INFO, "Models loaded");
				callback.setModels(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.INFO,
						"Unable to load models, sending notification. "
								+ caught.getMessage());
				eventBus.fireEvent(new NotificationEvent(
						"Unable to load models", NotificationType.ERROR));
			}
		});
	}

	public void loadModel(String modelId, final ModelCallback callback) {
		logger.log(Level.INFO, "Loading model " + modelId);
		try {
			long id = Long.parseLong(modelId);

			modelService.getModel(id, new AsyncCallback<Model>() {

				@Override
				public void onSuccess(Model model) {
					callback.setModel(model);
				}

				@Override
				public void onFailure(Throwable caught) {
					if (caught instanceof ModelException) {
						ModelException e = (ModelException) caught;
						eventBus.fireEvent(new NotificationEvent(
								"Unable to load models because of "
										+ e.getErrorCode(),
								NotificationType.ERROR));
					} else {
						eventBus.fireEvent(new NotificationEvent(
								"Unable to load models", NotificationType.ERROR));
					}
				}
			});
		} catch (NumberFormatException e) {
			eventBus.fireEvent(new NotificationEvent("Wrong model id format",
					NotificationType.ERROR));
		}
	}

	public interface ModelsCallback {
		void setModels(List<Model> models);
	}

	public interface ModelCallback {
		void setModel(Model model);
	}
}