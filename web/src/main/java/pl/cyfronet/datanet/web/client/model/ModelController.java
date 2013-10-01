package pl.cyfronet.datanet.web.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.event.model.ModelDeletedEvent;
import pl.cyfronet.datanet.web.client.event.model.NewModelEvent;
import pl.cyfronet.datanet.web.client.event.notification.GenericNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.ModelNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ModelController {

	private static final Logger logger = LoggerFactory
			.getLogger(ModelTreePanelPresenter.class.getName());

	private ModelServiceAsync modelService;
	private EventBus eventBus;
	private MessageAwareModelValidator modelValidator;

	private List<ModelProxy> models;

	@Inject
	public ModelController(ModelServiceAsync modelService,
			MessageAwareModelValidator modelValidator, EventBus eventBus) {
		this.modelService = modelService;
		this.modelValidator = modelValidator;
		this.eventBus = eventBus;
	}

	public void getModels(final ModelsCallback callback, boolean forceRefresh) {
		if (models == null || forceRefresh) {
			loadModels(callback);
		} else {
			callback.setModels(models);
		}
	}

	private void loadModels(final ModelsCallback callback) {
		logger.debug("Loading user models");
		modelService.getModels(new AsyncCallback<List<Model>>() {
			@Override
			public void onSuccess(List<Model> result) {
				logger.debug("Models loaded");
				models = new ArrayList<ModelProxy>();
				for (Model model : result) {
					models.add(new ModelProxy(model));
				}
				callback.setModels(models);
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.warn("Unable to load models, sending notification. {}",
						caught.getMessage());
				eventBus.fireEvent(new NotificationEvent(
						ModelNotificationMessage.modelListLoadError,
						NotificationType.ERROR));
			}
		});
	}

	public void getModel(final Long modelId, final ModelCallback callback) {
		getModels(new ModelsCallback() {
			@Override
			public void setModels(List<ModelProxy> models) {
				ModelProxy model = getCachedModel(modelId);
				
				if (model == null) {
					loadModel(modelId, callback);
				} else {
					callback.setModel(model);
				}
			}
		}, false);
	}

	private ModelProxy getCachedModel(Long modelId) {
		if (models != null) {
			for (ModelProxy m : models) {
				if (modelId.equals(m.getId())) {
					return m;
				}
			}
		}
		
		return null;
	}

	private void loadModel(Long modelId, final ModelCallback callback) {
		logger.debug("Loading model {}", modelId);
		
		try {
			modelService.getModel(modelId, new AsyncCallback<Model>() {
				@Override
				public void onSuccess(Model model) {
					callback.setModel(new ModelProxy(model));
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

	public void createNewModel(String modelName, final ModelCallback callback) {
		final ModelProxy newModel = new ModelProxy(new Model(),
				System.currentTimeMillis());
		newModel.setName(modelName);
		newModel.setDirty(true);
		getModels(new ModelsCallback() {
			@Override
			public void setModels(List<ModelProxy> models) {
				models.add(0, newModel);
				eventBus.fireEvent(new NewModelEvent(newModel.getId()));
				callback.setModel(newModel);
			}
		}, false);
	}

	public void saveModel(Long id, final ModelCallback callback) {
		getModel(id, new ModelCallback() {
			@Override
			public void setModel(final ModelProxy modelProxy) {
				List<String> modelErrors = modelValidator
						.validateModel(modelProxy.getModel());

				if (modelErrors.isEmpty()) {
					saveModel(modelProxy, callback);
				} else {
					eventBus.fireEvent(new NotificationEvent(
							ModelNotificationMessage.modelSaveError,
							NotificationType.ERROR, modelErrors.get(0)));
				}
			}
		});
	}

	private void saveModel(final ModelProxy modelProxy, final ModelCallback callback) {
		modelProxy.setTimestamp(new Date());
		modelService.saveModel(modelProxy.getModel(),
				new AsyncCallback<Model>() {
					@Override
					public void onSuccess(Model result) {
						ModelProxy proxy = modelProxy;
						
						if (modelProxy.isNew()) {
							int index = models.indexOf(modelProxy);
							models.remove(index);
							proxy = new ModelProxy(result);
							models.add(index, proxy);
						}
						
						proxy.setDirty(false);
						eventBus.fireEvent(new ModelChangedEvent(result.getId()));
						eventBus.fireEvent(new NotificationEvent(
								ModelNotificationMessage.modelSaved,
								NotificationType.SUCCESS));

						if (callback != null) {
							callback.setModel(proxy);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						NotificationEvent notificationEvent = null;
						
						if (caught instanceof ModelException) {
							ModelException modelException = (ModelException) caught;
							
							if (modelException.getErrorCode() == Code.ModelNameNotUnique) {
								notificationEvent = new NotificationEvent(
										ModelNotificationMessage.modelNameNotUnique,
										NotificationType.ERROR);
							}
						}
						
						if (notificationEvent == null) {
							notificationEvent = new NotificationEvent(
									ModelNotificationMessage.modelSaveError,
									NotificationType.ERROR, caught.getMessage());
						}
						
						eventBus.fireEvent(notificationEvent);
					}
				});
	}

	public void deleteModel(final Long modelId, final Command nextCallback, final Command errorCallback) {
		getModel(modelId, new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				models.remove(model);
				if (model.isNew()) {
					modelDeleted();
				} else {
					modelService.deleteModel(modelId,
							new AsyncCallback<Void>() {
								@Override
								public void onSuccess(Void result) {
									modelDeleted();
								}

								@Override
								public void onFailure(Throwable t) {
									errorCallback.execute();
									eventBus.fireEvent(new NotificationEvent(
											GenericNotificationMessage.rpcError,
											NotificationType.ERROR, t
													.getLocalizedMessage()));
								}
							});
				}
			}
			
			private void modelDeleted() {
				eventBus.fireEvent(new ModelDeletedEvent(modelId));
				nextCallback.execute();
			}
		});
	}

	public interface ModelsCallback {
		void setModels(List<ModelProxy> models);
	}

	public interface ModelCallback {
		void setModel(ModelProxy model);
	}
}