package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

public interface EntityRowDataProviderFactory {
	EntityRowDataProvider create(long repositoryId, String entityName, Presenter presenter);
}