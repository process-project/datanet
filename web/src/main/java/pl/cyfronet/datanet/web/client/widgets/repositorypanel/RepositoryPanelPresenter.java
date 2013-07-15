package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import pl.cyfronet.datanet.model.beans.Repository;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class RepositoryPanelPresenter implements Presenter {
	private Repository repository;
	private static final String REPO_TEMPLATE = "http://{repo}.datanet.cyfronet.pl";
	private View view;

	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getRepositoryContainer();
		void setRepositoryName(String name);
		void setRepositoryLink(String link);
	}
	
	public RepositoryPanelPresenter(View view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
		view.setRepositoryName(repository.getName());
		view.setRepositoryLink(REPO_TEMPLATE.replace("{repo}", repository.getName()));
	}
	
	public Repository getRepository() {
		return repository;
	}

	public IsWidget getWidget() {
		return view;
	}
}