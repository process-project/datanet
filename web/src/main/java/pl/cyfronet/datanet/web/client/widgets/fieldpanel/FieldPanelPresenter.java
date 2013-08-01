package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class FieldPanelPresenter implements Presenter {
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
	public FieldPanelPresenter(@Assisted EntityPanelPresenter entityPanelPresenter, View view) {
		this.view = view;
		view.setPresenter(this);
		this.entityPanelPresenter = entityPanelPresenter;
		field = new Field();
		view.selectType(field.getType().typeName());
		view.setRequired(field.isRequired());
		view.setTypes(getTypes());
	}	

	public void setField(Field field) {
		this.field = field;
		view.setName(field.getName());
		view.selectType(field.getType().typeName());
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
		field.setType(Type.typeValueOf(typeName));
		fieldModified();
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
			types.add(type.typeName());
		}
		return types;
	}
}