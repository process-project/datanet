package pl.cyfronet.datanet.web.client.event.model;

import com.google.gwt.event.shared.GwtEvent;

public class ModelChangedEvent extends GwtEvent<ModelChangedEventHandler> {
	public static Type<ModelChangedEventHandler> TYPE = new Type<ModelChangedEventHandler>();
	private Long modelId;

	public ModelChangedEvent(Long modelId) {
		this.modelId = modelId;
	}

	@Override
	public Type<ModelChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModelChangedEventHandler handler) {
		handler.onModelChangedEvent(this);
	}

	public Long getModelId() {
		return modelId;
	}
}
