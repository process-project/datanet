package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

public class FieldPanelPresenter implements Presenter {
	interface FieldPanelEventBinder extends EventBinder<FieldPanelPresenter> {}
	private final FieldPanelEventBinder eventBinder = GWT.create(FieldPanelEventBinder.class);
	
	private static final String ARRAY = "[]";

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void setName(String fieldName);
		void selectType(String typeName);
		void setRequired(boolean required);
		void setTypes(List<String> types);
	}

	private View view;
	private Field field;
	private EntityPanelPresenter entityPanelPresenter;

	@Inject
	public FieldPanelPresenter(
			@Assisted EntityPanelPresenter entityPanelPresenter, View view,
			EventBus eventBus) {
		this.view = view;
		view.setPresenter(this);
		this.entityPanelPresenter = entityPanelPresenter;
		field = new Field();
		view.setRequired(field.isRequired());
		updateTypes();

		eventBinder.bindEventHandlers(this, eventBus);
	}

	public void setField(Field field) {
		this.field = field;
		view.setName(field.getName());
		view.selectType(field.getTypeName());
		view.setRequired(field.isRequired());
	}

	public Field getField() {
		return field;
	}

	@Override
	public IsWidget getWidget() {
		return view;
	}

	@Override
	public void onRemoveField() {
		entityPanelPresenter.removeField(this);
	}

	@Override
	public void onFieldNameChanged(String fieldName) {
		field.setName(fieldName);
		fieldModified();
	}

	@Override
	public void onFieldTypeChanged(String typeName) {
		Type type = Type.typeValueOf(typeName);
		field.setType(type);

		if (isRelationType(type)) {
			String entityName = getEntityName(typeName);
			field.setTarget(entityPanelPresenter.getEntity(entityName));
		}

		fieldModified();
	}

	private boolean isRelationType(Type type) {
		return type == Type.ObjectId || type == Type.ObjectIdArray;
	}
	
	private String getEntityName(String typeName) {
		if (typeName.endsWith(ARRAY)) {
			return typeName.substring(0, typeName.length() - ARRAY.length());
		}
		
		return typeName;
	}	
	
	@Override
	public void onFieldRequiredChanged(boolean value) {
		field.setRequired(value);
		fieldModified();
	}

	private void fieldModified() {
		entityPanelPresenter.entityChanged();
	}

	private List<String> getTypes() {
		List<String> types = new ArrayList<String>();
		for (Type type : Type.values()) {
			if (!isRelationType(type)) {
				types.add(type.typeName());
			}
		}
		List<String> entitiesNames = entityPanelPresenter.getEntitiesNames();
		for (String entityName : entitiesNames) {
			types.add(entityName);
			types.add(entityName + ARRAY);
		}

		return types;
	}

	@EventHandler
	void onModelChanged(final ModelChangedEvent event) {
		updateTypes();
	}

	private void updateTypes() {
		view.setTypes(getTypes());
		view.selectType(getTypeName());
	}
	
	private String getTypeName() {
		if(isRelationType(field.getType())) {
			String entityName = field.getTarget().getName();
			if(field.getType() == Type.ObjectIdArray) {
				entityName = entityName + ARRAY;
			}
			return entityName;
		}
		return field.getType().typeName();
	}
}