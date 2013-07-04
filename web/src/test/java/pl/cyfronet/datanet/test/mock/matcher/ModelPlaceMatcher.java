package pl.cyfronet.datanet.test.mock.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;

public class ModelPlaceMatcher extends BaseMatcher<ModelPlace> {

	private long modelId;

	public ModelPlaceMatcher(long modelId) {
		this.modelId = modelId;
	}

	@Override
	public boolean matches(Object item) {
		if (item instanceof ModelPlace) {
			return ((ModelPlace) item).getModelId() == modelId;
		}

		return false;
	}

	@Override
	public void describeTo(Description description) {

	}
}
