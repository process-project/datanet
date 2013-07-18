package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Version;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelServiceAsync {
	void saveModel(Model model, AsyncCallback<Model> asyncCallback);

	void getModels(AsyncCallback<List<Model>> asyncCallback);

	void getModel(long modelId, AsyncCallback<Model> callback);

	void deleteModel(long modelId, AsyncCallback<Void> callback);
	
	void getVersions(long modelId, AsyncCallback<List<Version>> callback);
	
	void getVersion(long versionId, AsyncCallback<Version> callback);

	void addVersion(long modelId, Version version, AsyncCallback<Version> callback);

}