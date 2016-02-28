package pl.cyfronet.datanet.web.client.di.factory;

import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityRowDataProvider;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.Presenter;

public interface EntityRowDataProviderFactory {
	EntityRowDataProvider create(long repositoryId, String entityName, Presenter presenter);
}