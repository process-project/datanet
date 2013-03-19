package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelServiceAsync {
	void saveModel(Model model, AsyncCallback<Model> asyncCallback);
	void getModels(AsyncCallback<List<Model>> asyncCallback);
	void deployModel(Model model, AsyncCallback<Void> asyncCallback);
	void getRepositories(AsyncCallback<List<String>> asyncCallback);
}