package pl.cyfronet.datanet.web.client.mvp;

import pl.cyfronet.datanet.web.client.di.factory.ModelActivityFactory;
import pl.cyfronet.datanet.web.client.mvp.activity.NewModelActivity;
import pl.cyfronet.datanet.web.client.mvp.activity.WelcomeActivity;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.NewModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class AppActivityMapper implements ActivityMapper {

	private ModelActivityFactory modelActivityFactory;

	@Inject
	public AppActivityMapper(ModelActivityFactory modelActivityFactory) {
		this.modelActivityFactory = modelActivityFactory;
	}
	
	@Override
	public Activity getActivity(Place place) {
		GWT.log("Get activity for " + place);
		if (place instanceof WelcomePlace) {
			return new WelcomeActivity();
		} else if(place instanceof ModelPlace) {
			return modelActivityFactory.create((ModelPlace) place);
		} else if(place instanceof NewModelPlace) {
			return new NewModelActivity();
		}
		return null;
	}
}
