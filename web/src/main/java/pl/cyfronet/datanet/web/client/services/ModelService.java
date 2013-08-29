package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.errors.ModelException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/modelService")
public interface ModelService extends RemoteService {
	Model saveModel(Model model) throws ModelException;
	List<Model> getModels() throws ModelException;
	Model getModel(long modelId) throws ModelException;
	void deleteModel(long modelId) throws ModelException;
	Version addVersion(long modelId, Version version) throws ModelException;
	List<Version> getVersions(long modelId) throws ModelException;
	Version getVersion(long versionId) throws ModelException;
	void removeVersion(long versionId) throws ModelException;
}