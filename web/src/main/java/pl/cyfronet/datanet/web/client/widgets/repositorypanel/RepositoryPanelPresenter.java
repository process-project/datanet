package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class RepositoryPanelPresenter implements Presenter {
	private String repository;
	private View view;

	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getRepositoryContainer();
		void setRepositoryName(String name);
	}
	
	public RepositoryPanelPresenter(View view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setRepository(String repositoryName) {
		repository = repositoryName;
		view.setRepositoryName(repositoryName);
	}
	
	public String getRepository() {
		return repository;
	}

	public IsWidget getWidget() {
		return view;
	}
}