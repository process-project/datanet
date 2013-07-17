package pl.cyfronet.datanet.web.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.event.model.VersionReleasedEvent;
import pl.cyfronet.datanet.web.client.event.notification.ModelNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class VersionController {

	private static final Logger logger = Logger
			.getLogger(ModelTreePanelPresenter.class.getName());

	private ModelServiceAsync modelService;
	private EventBus eventBus;
	
	private Map<Long, List<Version>> versions;

	private ModelController modelController;

	@Inject
	public VersionController(ModelServiceAsync modelService, ModelController modelController, EventBus eventBus) {
		this.modelService = modelService;
		this.eventBus = eventBus;
		this.modelController = modelController;
		this.versions = new HashMap<Long, List<Version>>();
	}

	public void getVersions(long modelId, final VersionsCallback callback, boolean forceRefresh) {
		if (versions.get(modelId) == null || forceRefresh)
			loadVersions(modelId, callback);
		else
			callback.setVersions(versions.get(modelId));
	}
	
	public void getVersion(final Long versionId, final VersionCallback callback) {
		modelService.getVersion(versionId, new AsyncCallback<Version>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.INFO,
						"Unable to load version " + versionId + ", sending notification. "
								+ caught.getMessage());
				eventBus.fireEvent(new NotificationEvent(
						ModelNotificationMessage.versionLoadError,
						NotificationType.ERROR));
			}

			@Override
			public void onSuccess(final Version result) {
				logger.log(Level.INFO, "Version " + versionId + " loaded");
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

	private void loadVersions(final Long modelId,  final VersionsCallback callback) {
		logger.log(Level.INFO, "Loading user models");
		
		modelService.getVersions(modelId, new AsyncCallback<List<Version>>() {

			@Override
			public void onSuccess(List<Version> result) {
				logger.log(Level.INFO, "Versions for model " + modelId + " loaded");
				if (result != null && result.size() > 0)
					versions.put(modelId, result);
				else 
					versions.put(modelId, result);
				callback.setVersions(versions.get(modelId));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.INFO,
						"Unable to load versions for model " + modelId + ", sending notification. "
								+ caught.getMessage());
				eventBus.fireEvent(new NotificationEvent(
						ModelNotificationMessage.versionListLoadError,
						NotificationType.ERROR));
			}

		});
	}

	public void releaseNewVersion(final Long modelId, final VersionCallback callback) {
		
		modelController.getModel(modelId, new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				if (model.isDirty())
					eventBus.fireEvent(new NotificationEvent(
							ModelNotificationMessage.versionReleaseError,
							NotificationType.NOTE, "model is not saved"));
				else 
					releaseNewVersion(model, callback);
			}

		});
		
	}
	
	private void releaseNewVersion(ModelProxy model, final VersionCallback callback) {
		Version version = new Version(model, model.getName() + " " + new Date().toString()); // TODO add version name 
		
		modelService.addVersion(model.getId(), version, new AsyncCallback<Version>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new NotificationEvent(
						ModelNotificationMessage.versionReleaseError,
						NotificationType.ERROR, caught.getMessage()));
			}

			@Override
			public void onSuccess(Version result) {
				if (result == null)
					eventBus.fireEvent(new NotificationEvent(
							ModelNotificationMessage.versionReleaseError,
							NotificationType.ERROR, "Returned version is null"));
				else {
					if (versions.get(result.getModelId()) == null)
						versions.put(result.getModelId(), new ArrayList<Version>());
					versions.get(result.getModelId()).add(result);
					eventBus.fireEvent(new VersionReleasedEvent(result.getModelId(), result.getId()));
					eventBus.fireEvent(new NotificationEvent(
							ModelNotificationMessage.versionReleased,
							NotificationType.SUCCESS));
				}
				if (callback != null)
					callback.setVersion(result);
			}
			
		});
	}
	
	public interface VersionsCallback {
		void setVersions(List<Version> versions);
	}
	
	public interface VersionCallback {
		void setVersion(Version version);
	}

}