package pl.cyfronet.datanet.web.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.errors.ModelException;

@RemoteServiceRelativePath("../rpcservices/repositoryService")
public interface RepositoryService extends RemoteService {
	void deployModel(Model model) throws ModelException;
	List<Repository> getRepositories() throws ModelException;
	void undeployRepository(long repositoryId) throws ModelException;
	Repository getRepository(long repositoryId);
	List<Map<String, String>> getData(long repositoryId, String entityName, int start, int length);
}