package pl.cyfronet.datanet.web.client.event.model;

import com.google.web.bindery.event.shared.Event;

public class NewModelEvent extends Event<NewModelEventHandler> {
	public static Type<NewModelEventHandler> TYPE = new Type<NewModelEventHandler>();
	private Long modelId;

	public NewModelEvent(Long modelId) {
		this.modelId = modelId;
	}

	@Override
	public Type<NewModelEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NewModelEventHandler handler) {
		handler.onNewModelEvent(this);
	}

	public Long getModelId() {
		return modelId;
	}
}
