package pl.cyfronet.datanet.web.client.mvp.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ModelPlace extends Place {
	private Long modelId;

	public ModelPlace(Long modelId) {
		this.modelId = modelId;
	}

	public Long getModelId() {
		return modelId;
	}

	public static class Tokenizer implements PlaceTokenizer<ModelPlace> {
		@Override
		public String getToken(ModelPlace place) {
			return String.valueOf(place.getModelId());
		}

		@Override
		public ModelPlace getPlace(String token) {
			try {
				return new ModelPlace(Long.valueOf(token));
			} catch (NumberFormatException e) {
				return new ModelPlace(null);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelPlace other = (ModelPlace) obj;
		if (modelId == null) {
			if (other.modelId != null)
				return false;
		} else if (!modelId.equals(other.modelId))
			return false;
		return true;
	}
}
