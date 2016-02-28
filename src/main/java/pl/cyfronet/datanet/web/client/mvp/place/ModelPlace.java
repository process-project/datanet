package pl.cyfronet.datanet.web.client.mvp.place;

public class ModelPlace extends TokenizablePlace {
	private Long modelId;

	public ModelPlace(Long modelId) {
		this.modelId = modelId;
	}

	public Long getModelId() {
		return modelId;
	}

	public static class Tokenizer extends GenericTokenizer<ModelPlace> {
		@Override
		ModelPlace createPlaceInstance(Long itemId) {
			return new ModelPlace(itemId);
		}
	}
	
	@Override
	public Long getToken() {
		return modelId;
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
