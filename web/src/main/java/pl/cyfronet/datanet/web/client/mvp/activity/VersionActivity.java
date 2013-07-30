package pl.cyfronet.datanet.web.client.mvp.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelWidget;

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

	@Inject
	public VersionActivity(VersionController versionController,
			@Assisted Long versionId) {
		this.versionController = versionController;
		this.versionId = versionId;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		logger.info("Loading version {}", versionId);
		versionController.getVersion(versionId, new VersionCallback() {
			@Override
			public void setVersion(Version version) {
				VersionPanelPresenter presenter = new VersionPanelPresenter(
						new VersionPanelWidget());
				presenter.setVersion(version);
				panel.setWidget(presenter.getWidget());
			}
		});
	}
}
