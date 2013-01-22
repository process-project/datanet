package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

public class FieldPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasText getName();
		void selectType(Type type);
		void setEditable(boolean editable);
	}
	
	private View view;
	private Field field;
	private EntityPanelPresenter entityPanelPresenter;
	
	public FieldPanelPresenter(EntityPanelPresenter entityPanelPresenter, FieldPanelWidget view) {
		this.view = view;
		view.setPresenter(this);
		this.entityPanelPresenter = entityPanelPresenter;
		field = new Field();
		view.selectType(field.getType());
	}
	
	public void setField(Field field) {
		this.field = field;
		view.getName().setText(field.getName());
		view.selectType(field.getType());
		
		if(field.getType() == Type.Id) {
			view.setEditable(false);
		}
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
	}

	@Override
	public void onFieldTypeChanged(Type value) {
		field.setType(value);
	}
}