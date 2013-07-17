package pl.cyfronet.datanet.web.client.mvp;

import pl.cyfronet.datanet.web.client.di.factory.ModelActivityFactory;
import pl.cyfronet.datanet.web.client.di.factory.VersionActivityFactory;
import pl.cyfronet.datanet.web.client.mvp.activity.WelcomeActivity;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class AppActivityMapper implements ActivityMapper {

	private ModelActivityFactory modelActivityFactory;
	private VersionActivityFactory versionActivityFactory;
	private Place currentPlace;
	private Activity currentActivity;

	@Inject
	public AppActivityMapper(ModelActivityFactory modelActivityFactory, VersionActivityFactory versionActivityFactory) {
		this.modelActivityFactory = modelActivityFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		Activity activity = null;
		if (place instanceof WelcomePlace) {
			activity = new WelcomeActivity();
		} else if (place instanceof ModelPlace) {
			if (place.equals(currentPlace)) {
				activity = currentActivity;
			} else {
				activity = modelActivityFactory.create(((ModelPlace) place)
						.getModelId());
			}
		}
		else if (place instanceof VersionPlace) {
			if (place.equals(currentPlace)) {
				activity = currentActivity;
			} else {
				VersionPlace versionPlace = (VersionPlace) place;
				activity = versionActivityFactory.create(versionPlace.getVersionId());
			}
		}
		currentActivity = activity;
		currentPlace = place;
		return activity;
	}
}
