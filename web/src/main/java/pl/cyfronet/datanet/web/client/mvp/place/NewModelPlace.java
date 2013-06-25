package pl.cyfronet.datanet.web.client.mvp.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class NewModelPlace extends Place {	
	
	public static class Tokenizer implements PlaceTokenizer<NewModelPlace> {
		@Override
		public String getToken(NewModelPlace place) {
			return "unnamed";
		}

		@Override
		public NewModelPlace getPlace(String token) {
			return new NewModelPlace();
		}
	}
}
