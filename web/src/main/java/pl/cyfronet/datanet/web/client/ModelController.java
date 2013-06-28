package pl.cyfronet.datanet.web.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.event.notification.ModelNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
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

	private List<Model> models;

	@Inject
	public ModelController(ModelServiceAsync modelService, EventBus eventBus) {
		this.modelService = modelService;
		this.eventBus = eventBus;
	}

	public void getModels(final ModelsCallback callback, boolean forceRefresh) {
		if (models == null || forceRefresh) {
			loadModels(callback);
		} else {
			callback.setModels(models);
		}
	}

	public void loadModels(final ModelsCallback callback) {
		logger.log(Level.INFO, "Loading user models");
		modelService.getModels(new AsyncCallback<List<Model>>() {
			@Override
			public void onSuccess(List<Model> result) {
				logger.log(Level.INFO, "Models loaded");
				models = result;
				callback.setModels(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.INFO,
						"Unable to load models, sending notification. "
								+ caught.getMessage());
				eventBus.fireEvent(new NotificationEvent(
						ModelNotificationMessage.modelListLoadError,
						NotificationType.ERROR));
			}
		});
	}

	public void getModel(Long modelId, final ModelCallback callback) {
		Model model = getCachedModel(modelId);

		if (model == null) {
			loadModel(modelId, callback);
		} else {
			callback.setModel(model);
		}
	}

	private Model getCachedModel(Long modelId) {
		if (models != null) {
			for (Model m : models) {
				if (modelId.equals(m.getId())) {
					return m;
				}
			}
		}
		return null;
	}

	public void loadModel(Long modelId, final ModelCallback callback) {
		logger.log(Level.INFO, "Loading model " + modelId);
		try {
			modelService.getModel(modelId, new AsyncCallback<Model>() {

				@Override
				public void onSuccess(Model model) {
					callback.setModel(model);
				}

				@Override
				public void onFailure(Throwable caught) {
					if (caught instanceof ModelException) {
						ModelException e = (ModelException) caught;
						eventBus.fireEvent(new NotificationEvent(
								ModelNotificationMessage.modelLoadError,
								NotificationType.ERROR, e.getErrorCode().name()));
					} else {
						eventBus.fireEvent(new NotificationEvent(
								ModelNotificationMessage.modelLoadErrorNoCause,
								NotificationType.ERROR));
					}
				}
			});
		} catch (NumberFormatException e) {
			eventBus.fireEvent(new NotificationEvent(
					ModelNotificationMessage.modelWrongIdFormat,
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