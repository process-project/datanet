package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.DataCallback;

public interface Presenter {
	void getEntityRows(String entityName, int start, int length, DataCallback dataCallback);
}