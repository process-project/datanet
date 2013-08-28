package pl.cyfronet.datanet.web.client.services;

import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RepositoryServiceAsync {
	void getRepositories(AsyncCallback<List<Repository>> asyncCallback);
	void undeployRepository(long repositoryId, AsyncCallback<Void> asyncCallback);
	void getRepository(long repositoryId, AsyncCallback<Repository> asyncCallback);
	void getData(long repositoryId, String entityName, int start, int length, Map<String, String> query, AsyncCallback<EntityData> asyncCallback);
	void getRepositories(long versionId, AsyncCallback<List<Repository>> asyncCallback);
	void deployModelVersion(long versionId, AsyncCallback<Repository> asyncCallback);
	void saveData(long repositoryId, String entityName, Map<String, String> data, AsyncCallback<Void> asyncCallback);
	void removeRepository(long repositoryId, AsyncCallback<Void> asyncCallback);
}