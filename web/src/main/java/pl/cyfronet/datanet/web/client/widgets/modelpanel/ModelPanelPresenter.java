package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelWidget;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class ModelPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getEntityContainer();
		void setModelName(String name);
		void setModelVersion(String version);
	}
	
	private View view;
	private List<EntityPanelPresenter> entityPanelPresenters;
	private Model model;
	
	public ModelPanelPresenter(View view) {
		this.view = view;
		view.setPresenter(this);
		entityPanelPresenters = new ArrayList<EntityPanelPresenter>();
		model = new Model();
	}

	public IsWidget getWidget() {
		return view;
	}
	
	public void removeEntity(EntityPanelPresenter entityPanelPresenter) {
		view.getEntityContainer().remove(entityPanelPresenter.getWidget().asWidget());
		entityPanelPresenters.remove(entityPanelPresenter);
		model.getEntities().remove(entityPanelPresenter.getEntity());
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void onNewEntity() {
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(this, new EntityPanelWidget());
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
		model.getEntities().add(entityPanelPresenter.getEntity());
	}

	@Override
	public void onModelNameChanged(String modelName) {
		model.setName(modelName);
	}

	@Override
	public void onModelVersionChanged(String versionName) {
		model.setVersion(versionName);
	}
	
	public void setModel(Model model) {
		this.model = model;
		view.setModelName(model.getName());
		view.setModelVersion(model.getVersion());
		view.getEntityContainer().clear();
		entityPanelPresenters.clear();
		
		for(Entity entity : model.getEntities()) {
			displayEntity(entity);
		}
	}

	private void displayEntity(Entity entity) {
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(this, new EntityPanelWidget());
		entityPanelPresenter.setEntity(entity);
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
	}
}