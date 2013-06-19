package pl.cyfronet.datanet.web.client.mvp.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ModelPlace extends Place {
	private String modelId;

	public ModelPlace(String modelId) {
		this.modelId = modelId;
	}

	public String getModelId() {
		return modelId;
	}
	
	public static class Tokenizer implements PlaceTokenizer<ModelPlace> {
		@Override
		public String getToken(ModelPlace place) {
			return place.getModelId();
		}

		@Override
		public ModelPlace getPlace(String token) {
			return new ModelPlace(token);
		}
	}
}
