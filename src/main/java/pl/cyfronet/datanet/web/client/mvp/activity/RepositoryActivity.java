package pl.cyfronet.datanet.web.client.mvp.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class RepositoryActivity extends AbstractActivity {
	private static final Logger log = LoggerFactory.getLogger(RepositoryActivity.class);
	
	private RepositoryPanelPresenter repositoryPanelPresenter;
	private long repositoryId;
	
	@Inject
	public RepositoryActivity(RepositoryPanelPresenter repositoryPanelPresenter, @Assisted long repositoryId) {
		this.repositoryPanelPresenter = repositoryPanelPresenter;
		this.repositoryId = repositoryId;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		log.debug("Loading repository with id: {}", repositoryId);
		repositoryPanelPresenter.setRepository(repositoryId);
		panel.setWidget(repositoryPanelPresenter.getWidget());
	}
}