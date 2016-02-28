package pl.cyfronet.datanet.web.client.mvp.place;

import com.google.gwt.place.shared.Place;

public abstract class TokenizablePlace extends Place {
	public abstract Long getToken();
	
}