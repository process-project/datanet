package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionsCallback;
import pl.cyfronet.datanet.web.client.di.factory.EntityPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ModelPanelPresenter implements Presenter {
	private static final Logger logger = LoggerFactory.getLogger(ModelPanelPresenter.class.getName());

	private List<EntityPanelPresenter> entityPanelPresenters;
	private ModelProxy model;
	private View view;
	private EventBus eventBus;
	private EntityPanelPresenterFactory entityPanelFactory;
	private VersionController versionController;
	private PlaceController placeController;

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getEntityContainer();
		void setModelName(String name);
		void showNewVersionModal();
		HasText getNewVersionText();
		void setNewVersionErrorState(boolean state);
		void setNewVersionBusyState(boolean busy);
		void hideNewVersionModal();
	}

	@Inject
	public ModelPanelPresenter(View view, EventBus eventBus,
			EntityPanelPresenterFactory entityPanelFactory, VersionController versionController, PlaceController placeController) {
		this.view = view;
		this.eventBus = eventBus;
		this.entityPanelFactory = entityPanelFactory;
		this.versionController = versionController;
		this.placeController = placeController;
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

		if (model.getEntities() != null) {
			for (Entity entity : model.getEntities()) {
				displayEntity(entity);
			}
		}
	}

	private void displayEntity(Entity entity) {
		logger.debug("Adding entity to model: " + entity);
		EntityPanelPresenter entityPanelPresenter = entityPanelFactory
				.create(this);
		entityPanelPresenter.setEntity(entity);
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(
				entityPanelPresenter.getWidget().asWidget());
	}

	@Override
	public void onNewEntity() {
		logger.debug("Adding new entity to model");
		EntityPanelPresenter entityPanelPresenter = entityPanelFactory
				.create(this);
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(
				entityPanelPresenter.getWidget().asWidget());
		model.getEntities().add(entityPanelPresenter.getEntity());

		modelChanged();
	}

	public void removeEntity(EntityPanelPresenter entityPanelPresenter) {
		logger.debug("Removing entity from model: "
				+ entityPanelPresenter.getEntity());
		view.getEntityContainer().remove(
				entityPanelPresenter.getWidget().asWidget());
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

	@Override
	public List<String> getEntitiesNames() {
		List<String> names = new ArrayList<String>();
		for (Entity entity : model.getEntities()) {
			if (!empty(entity.getName())) {
				names.add(entity.getName());
			}
		}

		return names;
	}

	private boolean empty(String str) {
		return str == null || str.equals("");
	}

	@Override
	public Entity getEntity(String entityName) {
		for (Entity entity : model.getEntities()) {
			if (entity.getName().equals(entityName)) {
				return entity;
			}
		}
		return null;
	}
	
	@Override
	public void onNewVersionModal() {
		view.showNewVersionModal();
	}

	@Override
	public void onCreateNewVersion() {
		final String newVersion = view.getNewVersionText().getText();
		
		if (newVersion.trim().isEmpty() || newVersion.trim().length() > 20) {
			view.setNewVersionErrorState(true);
		} else {
			versionController.getVersions(model.getId(), new VersionsCallback() {
				@Override
				public void setVersions(List<Version> versions) {
					boolean nameValid = true;
					for (Version version : versions) {
						if (version.getName().equals(newVersion)) {
							view.setNewVersionErrorState(true);
							nameValid = false;
							break;
						}
					}
					
					if (nameValid) {
						view.setNewVersionErrorState(false);
						view.setNewVersionBusyState(true);
						versionController.releaseNewVersion(model.getId(), newVersion, new VersionCallback() {
							@Override
							public void setVersion(Version version) {
								view.setNewVersionBusyState(false);
								view.hideNewVersionModal();
								placeController.goTo(new VersionPlace(version.getId()));
							}
						});
					}
				}
			}, false);
		}
	}
}