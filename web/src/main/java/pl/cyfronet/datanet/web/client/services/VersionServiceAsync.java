package pl.cyfronet.datanet.web.client.services;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Version;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VersionServiceAsync {
	void getVersions(long modelId, AsyncCallback<List<Version>> callback);
	void getVersion(long versionId, AsyncCallback<Version> callback);
	void addVersion(long modelId, Version version, AsyncCallback<Version> callback);
	void removeVersion(long versionId, AsyncCallback<Void> asyncCallback);
}