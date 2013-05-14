package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RepositoryServiceAsync {
	void deployModel(Model model, AsyncCallback<Void> asyncCallback);
	void getRepositories(AsyncCallback<List<String>> asyncCallback);
	void undeployRepository(String repositoryName, AsyncCallback<Void> asyncCallback);
}
