package pl.cyfronet.datanet.web.client.di.factory;

import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;

import com.google.gwt.activity.shared.Activity;

public interface ModelActivityFactory {
	Activity create(ModelPlace place);	
}
