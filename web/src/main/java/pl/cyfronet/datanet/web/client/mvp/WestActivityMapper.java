package pl.cyfronet.datanet.web.client.mvp;

import pl.cyfronet.datanet.web.client.mvp.activity.BrowserActivity;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.NewModelPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class WestActivityMapper implements ActivityMapper {

	private BrowserActivity browserActivity;

	@Inject
	public WestActivityMapper(BrowserActivity browserActivity) {
		this.browserActivity = browserActivity;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof ModelPlace) {
			browserActivity.setPlace((ModelPlace) place);
		} else if (place instanceof NewModelPlace) {
			browserActivity.setPlace((NewModelPlace) place);
		}
		return browserActivity;
	}

}
