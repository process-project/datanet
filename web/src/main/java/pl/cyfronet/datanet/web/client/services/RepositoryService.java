package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.errors.ModelException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/repositoryService")
public interface RepositoryService extends RemoteService {
	List<Repository> getRepositories() throws ModelException;
	void undeployRepository(long repositoryId) throws ModelException;
	void deployModelVersion(Version modelVersion, String repositoryName)	throws ModelException;
}