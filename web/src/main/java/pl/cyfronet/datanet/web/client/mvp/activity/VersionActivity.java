package pl.cyfronet.datanet.web.client.mvp.activity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionsCallback;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class VersionActivity extends AbstractActivity {

	private static final Logger logger = Logger.getLogger(VersionActivity.class
			.getName());

	private VersionController versionController;

	private Long versionId;
	private Long modelId;

	@Inject
	public VersionActivity(VersionController versionController,
			@Assisted Long modelId, @Assisted Long versionId) {
		this.versionController = versionController;
		this.modelId = modelId;
		this.versionId = versionId;
	}

	// TODO this method must be fixed 
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		logger.log(Level.INFO, "Loading model with id: " + modelId);
		//  TODO implement on server side 
		versionController.getVersions(modelId, new VersionsCallback() {
			@Override
			public void setVersions(List<Version> versions) {
				for (Version version : versions) {
					if (version.getId() == versionId) {
						panel.setWidget(new Widget());
						break; 
					}
				}
			}
			
		}, false);
	}
}
