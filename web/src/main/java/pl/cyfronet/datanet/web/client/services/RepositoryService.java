package pl.cyfronet.datanet.web.client.services;

import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.errors.RepositoryException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/repositoryService")
public interface RepositoryService extends RemoteService {
	List<Repository> getRepositories() throws RepositoryException;
	void undeployRepository(long repositoryId) throws RepositoryException;
	Repository getRepository(long repositoryId) throws RepositoryException;
	List<Map<String, String>> getData(long repositoryId, String entityName, int start, int length);
	void deployModelVersion(Version modelVersion, String repositoryName) throws RepositoryException;
	List<Repository> getRepositories(long versionId) throws RepositoryException;
}
