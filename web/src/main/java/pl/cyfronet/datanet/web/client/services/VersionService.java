package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.errors.VersionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../rpcservices/versionService")
public interface VersionService extends RemoteService {
	Version addVersion(long modelId, Version version) throws VersionException;
	List<Version> getVersions(long modelId) throws VersionException;
	Version getVersion(long versionId) throws VersionException;	
}
