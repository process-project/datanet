package pl.cyfronet.datanet.web.client.di.factory;

import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter;

public interface EntityDataPanelPresenterFactory {
	EntityDataPanelPresenter create(long repositoryId, String entityName);
}