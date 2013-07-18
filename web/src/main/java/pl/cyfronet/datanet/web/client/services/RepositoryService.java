package pl.cyfronet.datanet.web.client.services;

import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.errors.ModelException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/repositoryService")
public interface RepositoryService extends RemoteService {
	List<Repository> getRepositories() throws ModelException;
	void undeployRepository(long repositoryId) throws ModelException;
	Repository getRepository(long repositoryId);
	List<Map<String, String>> getData(long repositoryId, String entityName, int start, int length);
	void deployModelVersion(Version modelVersion, String repositoryName) throws ModelException;
}
