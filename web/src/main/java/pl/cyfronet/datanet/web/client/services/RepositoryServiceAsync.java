package pl.cyfronet.datanet.web.client.services;

import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RepositoryServiceAsync {
	void getRepositories(AsyncCallback<List<Repository>> asyncCallback);
	void undeployRepository(long repositoryId, AsyncCallback<Void> asyncCallback);
	void getRepository(long repositoryId, AsyncCallback<Repository> asyncCallback);
	void getData(long repositoryId, String entityName, int start, int length, AsyncCallback<List<Map<String, String>>> asyncCallback);
	void deployModelVersion(Version modelVersion, String repositoryName, AsyncCallback<Void> callback);
	void getRepositories(long versionId, AsyncCallback<List<Repository>> asyncCallback);
}
