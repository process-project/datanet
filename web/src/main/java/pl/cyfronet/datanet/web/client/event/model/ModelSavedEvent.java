package pl.cyfronet.datanet.web.client.event.model;

import com.google.gwt.event.shared.GwtEvent;

public class ModelSavedEvent extends GwtEvent<ModelSavedEventHandler> {
	public static Type<ModelSavedEventHandler> TYPE = new Type<ModelSavedEventHandler>();
	private Long modelId;

	public ModelSavedEvent(Long modelId) {
		this.modelId = modelId;
	}

	@Override
	public Type<ModelSavedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModelSavedEventHandler handler) {
		handler.onModelSavedEvent(this);
	}

	public Long getModelId() {
		return modelId;
	}
}
