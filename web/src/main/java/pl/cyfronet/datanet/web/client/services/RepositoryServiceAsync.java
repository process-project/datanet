package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RepositoryServiceAsync {
	void getRepositories(AsyncCallback<List<Repository>> asyncCallback);
	void undeployRepository(long repositoryId, AsyncCallback<Void> asyncCallback);

	void deployModelVersion(Version modelVersion, String repositoryName,	AsyncCallback<Void> callback);
}
