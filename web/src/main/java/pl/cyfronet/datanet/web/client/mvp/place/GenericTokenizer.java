package pl.cyfronet.datanet.web.client.mvp.place;

import com.google.gwt.place.shared.PlaceTokenizer;

public abstract class GenericTokenizer<T extends TokenizablePlace> implements PlaceTokenizer<T> {

	@Override
	public T getPlace(String token) throws NumberFormatException {
		try {
			return createPlaceInstance(Long.valueOf(token));
		} catch (NumberFormatException e) {
			return createPlaceInstance(null);
		}
	}
	
	abstract T createPlaceInstance(Long itemId);

	@Override
	public String getToken(T place) {
		return String.valueOf(place.getToken());
	}
}