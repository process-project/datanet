package pl.cyfronet.datanet.web.client.mvp.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class VersionActivity extends AbstractActivity {

	private static final Logger logger = LoggerFactory
			.getLogger(VersionActivity.class.getName());

	private VersionController versionController;

	private Long versionId;

	private VersionPanelPresenter versionPresenter;

	@Inject
	public VersionActivity(VersionController versionController, VersionPanelPresenter versionPresenter,
			@Assisted Long versionId) {
		this.versionController = versionController;
		this.versionPresenter = versionPresenter;
		this.versionId = versionId;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		logger.info("Loading version {}", versionId);
		versionController.getVersion(versionId, new VersionCallback() {
			@Override
			public void setVersion(Version version) {
				versionPresenter.setVersion(version);
				panel.setWidget(versionPresenter.getWidget());
			}
		});
	}
}
