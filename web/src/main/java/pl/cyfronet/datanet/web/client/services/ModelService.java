package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.errors.ModelException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/modelService")
public interface ModelService extends RemoteService {
	void saveModel(Model model) throws ModelException;
	List<Model> getModels() throws ModelException;
}