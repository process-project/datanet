package pl.cyfronet.datanet.web.client.services;

import pl.cyfronet.datanet.model.beans.Model;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelServiceAsync {
	void saveModel(Model model, AsyncCallback<Void> asyncCallback);
}