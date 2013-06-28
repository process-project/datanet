package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class EntityPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getFieldContainer();
		void setEntityName(String name);
	}

	private View view;
	private ModelPanelPresenter modelPanelPresenter;
	private List<FieldPanelPresenter> fieldPanelPresenters;
	private Entity entity;

	public EntityPanelPresenter(ModelPanelPresenter modelPanelPresenter, View view) {
		this.view = view;
		view.setPresenter(this);
		this.modelPanelPresenter = modelPanelPresenter;
		fieldPanelPresenters = new ArrayList<FieldPanelPresenter>();
		entity = new Entity();
		setEntity(entity);
	}

	public IsWidget getWidget() {
		return view;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
		view.setEntityName(entity.getName());
		view.getFieldContainer().clear();
		fieldPanelPresenters.clear();
		
		for(Field field : entity.getFields()) {
			FieldPanelPresenter fieldPanelPresenter = new FieldPanelPresenter(this, new FieldPanelWidget());
			fieldPanelPresenter.setField(field);
			fieldPanelPresenters.add(fieldPanelPresenter);
			view.getFieldContainer().add(fieldPanelPresenter.getWidget().asWidget());
		}
	}

	@Override
	public void onRemoveEntity() {
		modelPanelPresenter.removeEntity(this);		
	}

	@Override
	public void onNewField() {
		FieldPanelPresenter fieldPanelPresenter = new FieldPanelPresenter(this, new FieldPanelWidget());
		fieldPanelPresenters.add(fieldPanelPresenter);
		view.getFieldContainer().add(fieldPanelPresenter.getWidget().asWidget());
		entity.getFields().add(fieldPanelPresenter.getField());
		entityChanged();
	}

	public void removeField(FieldPanelPresenter fieldPanelPresenter) {
		view.getFieldContainer().remove(fieldPanelPresenter.getWidget().asWidget());
		fieldPanelPresenters.remove(fieldPanelPresenter);
		entityChanged();
	}
	
	@Override
	public void onEntityNameChanged(String entityName) {
		entity.setName(entityName);
		entityChanged();
	}
	
	public void entityChanged() {
		modelPanelPresenter.modelChanged();
	}
}