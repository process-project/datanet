package pl.cyfronet.datanet.web.client.mvp;

import pl.cyfronet.datanet.web.client.di.factory.ModelActivityFactory;
import pl.cyfronet.datanet.web.client.di.factory.RepositoryActivityFactory;
import pl.cyfronet.datanet.web.client.mvp.activity.WelcomeActivity;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class AppActivityMapper implements ActivityMapper {
	private ModelActivityFactory modelActivityFactory;
	private Place currentPlace;
	private Activity currentActivity;
	private RepositoryActivityFactory repositoryActivityFactory;

	@Inject
	public AppActivityMapper(ModelActivityFactory modelActivityFactory,
			RepositoryActivityFactory repositoryActivityFactory) {
		this.modelActivityFactory = modelActivityFactory;
		this.repositoryActivityFactory = repositoryActivityFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		Activity activity = null;
		
		if (place instanceof WelcomePlace) {
			//TODO(DH): replace with welcome activity when repository panel tests are finished
			activity = repositoryActivityFactory.create();
//			activity = new WelcomeActivity();
		} else if (place instanceof ModelPlace) {
			if (place.equals(currentPlace)) {
				activity = currentActivity;
			} else {
				activity = modelActivityFactory.create(((ModelPlace) place)
						.getModelId());
			}
		}
		
		currentActivity = activity;
		currentPlace = place;
		
		return activity;
	}
}