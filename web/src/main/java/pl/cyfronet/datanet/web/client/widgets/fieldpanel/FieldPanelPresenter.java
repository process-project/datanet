package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class FieldPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasText getName();
		void selectType(Type type);
		void setEditable(boolean editable);
		void setRequired(boolean required);
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
		view.selectType(field.getType());
		view.setRequired(field.isRequired());
	}

	public void setField(Field field) {
		this.field = field;
		view.getName().setText(field.getName());
		view.selectType(field.getType());
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
	public void onFieldTypeChanged(Type value) {
		field.setType(value);
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
}