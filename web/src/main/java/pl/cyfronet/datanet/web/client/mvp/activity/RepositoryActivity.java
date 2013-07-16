package pl.cyfronet.datanet.web.client.mvp.activity;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class RepositoryActivity extends AbstractActivity {
	private RepositoryPanelPresenter repositoryPanelPresenter;
	
	@Inject
	public RepositoryActivity(RepositoryPanelPresenter repositoryPanelPresenter) {
		this.repositoryPanelPresenter = repositoryPanelPresenter;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		//TODO(DH): get rid of this dummy implementation
		Repository repository = new Repository();
		repository.setName("Dummyrepository");
		repositoryPanelPresenter.setRepository(-1);
		panel.setWidget(repositoryPanelPresenter.getWidget());;
	}
}