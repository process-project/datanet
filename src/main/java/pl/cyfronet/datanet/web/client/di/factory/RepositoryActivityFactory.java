package pl.cyfronet.datanet.web.client.di.factory;

import com.google.gwt.activity.shared.Activity;

public interface RepositoryActivityFactory {
	Activity create(long repositoryId);
}