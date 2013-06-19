package pl.cyfronet.datanet.web.client.mvp;

import pl.cyfronet.datanet.web.client.mvp.activity.ModelActivity;
import pl.cyfronet.datanet.web.client.mvp.activity.WelcomeActivity;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	@Override
	public Activity getActivity(Place place) {
		GWT.log("Get activity for " + place);
		if (place instanceof WelcomePlace) {
			return new WelcomeActivity();
		} else if(place instanceof ModelPlace) {
			return new ModelActivity((ModelPlace) place);
		}
		return null;
	}
}
