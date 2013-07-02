package pl.cyfronet.datanet.web.client.mvp.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class NewModelPlace extends Place implements PlaceWithModel {

	private Long modelId;

	public NewModelPlace(Long modelId) {
		this.modelId = modelId;
	}

	public Long getModelId() {
		return modelId;
	}

	public static class Tokenizer implements PlaceTokenizer<NewModelPlace> {
		@Override
		public String getToken(NewModelPlace place) {
			return String.valueOf(place.getModelId());
		}

		@Override
		public NewModelPlace getPlace(String token) {
			try {
				return new NewModelPlace(Long.valueOf(token));
			} catch (NumberFormatException e) {
				return new NewModelPlace(null);
			}
		}
	}
}
