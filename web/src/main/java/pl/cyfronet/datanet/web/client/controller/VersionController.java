package pl.cyfronet.datanet.web.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.VersionNotificationMessage;
import pl.cyfronet.datanet.web.client.event.version.ModelVersionChangedEvent;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.services.VersionServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class VersionController {
	public interface VersionsCallback {
		void setVersions(List<Version> versions);
	}
	
	public interface VersionCallback {
		void setVersion(Version version);
	}
	
	private static final Logger log = LoggerFactory.getLogger(ModelTreePanelPresenter.class);

	private VersionServiceAsync versionService;
	private EventBus eventBus;
	private Map<Long, List<Version>> versions;
	private ModelController modelController;
	private PlaceController placeController;

	@Inject
	public VersionController(VersionServiceAsync versionService, ModelController modelController, EventBus eventBus, PlaceController placeController) {
		this.versionService = versionService;
		this.eventBus = eventBus;
		this.modelController = modelController;
		this.placeController = placeController;
		this.versions = new HashMap<Long, List<Version>>();
	}

	public void getVersions(long modelId, final VersionsCallback callback, boolean forceRefresh) {
		if (versions.get(modelId) == null || forceRefresh) {
			loadVersions(modelId, callback);
		}
		else {
			callback.setVersions(versions.get(modelId));
		}
	}
	
	public void getVersion(final Long versionId, final VersionCallback callback) {
		versionService.getVersion(versionId, new AsyncCallback<Version>() {
			@Override
			public void onFailure(Throwable caught) {
				log.info("Unable to load version {}, sending notification. Error message: {}", versionId, caught.getMessage());
				eventBus.fireEvent(new NotificationEvent(
						VersionNotificationMessage.versionLoadError,
						NotificationType.ERROR));
			}

			@Override
			public void onSuccess(final Version result) {
				log.info("Version {} loaded", versionId);
				long modelId = result.getModelId();
				// reload versions for model
				getVersions(modelId, new VersionsCallback() {
					@Override
					public void setVersions(List<Version> versions) {
						for (Version version : versions) {
							if (version.getId() == versionId) {
								callback.setVersion(version);
								break;
							}
						}
					}
				}, false);
			}
		});
	}

	/*
	 * Returns versions or empty list (should be safe even on failure)
	 */
	private void loadVersions(final Long modelId,  final VersionsCallback callback) {
		log.info("Loading user models");
		modelController.getModel(modelId, new ModelCallback() {	
			@Override
			public void setModel(ModelProxy model) {
				if (!model.isNew() && !model.isDirty())
					versionService.getVersions(modelId, new AsyncCallback<List<Version>>() {
						@Override
						public void onSuccess(List<Version> result) {
							log.info("Versions for model {} loaded", modelId);
							
							if (result != null && result.size() > 0)
								versions.put(modelId, result);
							else 
								versions.put(modelId, result);
							
							callback.setVersions(versions.get(modelId));
						}
						
						@Override
						public void onFailure(Throwable caught) {
							log.info("Unable to load versions for model {}, sending notification. Error message: {}", modelId, caught.getMessage());
							eventBus.fireEvent(new NotificationEvent(
									VersionNotificationMessage.versionListLoadError,
									NotificationType.ERROR));
							callback.setVersions(new ArrayList<Version>(0));
						}
					});
				else callback.setVersions(new ArrayList<Version>(0));
			}
		});
		
	}

	public void releaseNewVersion(final Long modelId, final String versionName, final VersionCallback callback) {
		modelController.getModel(modelId, new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				if (model.isDirty())
					eventBus.fireEvent(new NotificationEvent(
							VersionNotificationMessage.versionReleaseError,
							NotificationType.NOTE, "model is not saved"));
				else 
					releaseNewVersion(model, versionName, callback);
			}
		});
	}
	
	private void releaseNewVersion(ModelProxy model, String versionName, final VersionCallback callback) {
		Version version = new Version(model, versionName);
		versionService.addVersion(model.getId(), version, new AsyncCallback<Version>() {
			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new NotificationEvent(
						VersionNotificationMessage.versionReleaseError,
						NotificationType.ERROR, caught.getMessage()));
			}

			@Override
			public void onSuccess(Version result) {
				if (result == null) {
					eventBus.fireEvent(new NotificationEvent(
							VersionNotificationMessage.versionReleaseError,
							NotificationType.ERROR, "Returned version is null"));
				} else {
					if (versions.get(result.getModelId()) == null) {
						versions.put(result.getModelId(), new ArrayList<Version>());
					}
					
					versions.get(result.getModelId()).add(result);
					eventBus.fireEvent(new ModelVersionChangedEvent(result.getModelId(), result.getId()));
					eventBus.fireEvent(new NotificationEvent(
							VersionNotificationMessage.versionReleased,
							NotificationType.SUCCESS));
				}
				
				if (callback != null) {
					callback.setVersion(result);
				}
			}
		});
	}
	
	public void removeVersion(long versionId, final Command after) {
		getVersion(versionId, new VersionCallback() {
			@Override
			public void setVersion(final Version version) {
				versionService.removeVersion(version.getId(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						eventBus.fireEvent(new NotificationEvent(
								VersionNotificationMessage.versionRemoveError, NotificationType.ERROR, caught.getMessage()));
						
						if (after != null) {
							after.execute();
						}
					}

					@Override
					public void onSuccess(Void result) {
						versions.get(version.getModelId()).remove(version);
						eventBus.fireEvent(new NotificationEvent(
								VersionNotificationMessage.versionRemoved, NotificationType.SUCCESS));
						eventBus.fireEvent(new ModelVersionChangedEvent(version.getModelId(), (long) -1));
						placeController.goTo(new ModelPlace(version.getModelId()));
						
						if (after != null) {
							after.execute();
						}
					}
				});
			}
		});
	}
}