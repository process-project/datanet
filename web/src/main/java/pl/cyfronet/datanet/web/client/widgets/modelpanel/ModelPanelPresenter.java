package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelWidget;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

public class ModelPanelPresenter implements Presenter {
	private static final Logger logger = Logger
			.getLogger(ModelPanelPresenter.class.getName());
	
	private List<EntityPanelPresenter> entityPanelPresenters;
	private ModelProxy model;
	private View view;
	private EventBus eventBus;

	interface View extends IsWidget {
		void setPresenter(Presenter presenter);

		HasWidgets getEntityContainer();

		void setModelName(String name);

	}
	
	public ModelPanelPresenter(View view, EventBus eventBus) {
		this.view = view;
		this.eventBus = eventBus;
		entityPanelPresenters = new ArrayList<EntityPanelPresenter>();
		view.setPresenter(this);
	}

	public Model getModel() {
		return model;
	}
	
	public void setModel(ModelProxy model) {
		this.model = model;
		view.setModelName(model.getName());
		view.getEntityContainer().clear();
		entityPanelPresenters.clear();
		
		for(Entity entity : model.getEntities()) {
			displayEntity(entity);
		}
	}

	private void displayEntity(Entity entity) {
		logger.log(Level.INFO, "Adding entity to model: " + entity);
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(this, new EntityPanelWidget());
		entityPanelPresenter.setEntity(entity);
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
	}
	
	@Override
	public void onNewEntity() {
		logger.log(Level.INFO, "Adding new entity to model");
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(this, new EntityPanelWidget());
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
		model.getEntities().add(entityPanelPresenter.getEntity());
		
		modelChanged();
	}

	public void removeEntity(EntityPanelPresenter entityPanelPresenter) {
		logger.log(Level.INFO, "Removing entity from model: " + entityPanelPresenter.getEntity());
		view.getEntityContainer().remove(entityPanelPresenter.getWidget().asWidget());
		entityPanelPresenters.remove(entityPanelPresenter);
		model.getEntities().remove(entityPanelPresenter.getEntity());
		
		modelChanged();
	}
	
	@Override
	public void onModelNameChanged(String modelName) {
		model.setName(modelName);
		modelChanged();
	}

	public void modelChanged() {
		model.setDirty(true);
		eventBus.fireEvent(new ModelChangedEvent(model.getId()));
	}
	
	@Override
	public IsWidget getWidget() {
		return view;
	}
}
