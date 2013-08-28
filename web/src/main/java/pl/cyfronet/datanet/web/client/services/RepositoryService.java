package pl.cyfronet.datanet.web.client.services;

import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;
import pl.cyfronet.datanet.web.client.errors.RepositoryException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/repositoryService")
public interface RepositoryService extends RemoteService {
	List<Repository> getRepositories() throws RepositoryException;
	void undeployRepository(long repositoryId) throws RepositoryException;
	Repository getRepository(long repositoryId) throws RepositoryException;
	EntityData getData(long repositoryId, String entityName, int start, int length, Map<String, String> query) throws RepositoryException;
	List<Repository> getRepositories(long versionId) throws RepositoryException;
	Repository deployModelVersion(long versionId) throws RepositoryException;
	void saveData(long repositoryId, String entityName, Map<String, String> data) throws RepositoryException;
	void removeRepository(long repositoryId) throws RepositoryException;
}
