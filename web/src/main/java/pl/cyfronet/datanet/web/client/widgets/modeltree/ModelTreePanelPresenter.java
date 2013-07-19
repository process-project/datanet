package pl.cyfronet.datanet.web.client.widgets.modeltree;

import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isLoading;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isModel;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isRepository;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isRoot;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isVersion;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.callback.NextCallback;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoriesCallback;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionsCallback;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.event.model.NewModelEvent;
import pl.cyfronet.datanet.web.client.event.model.VersionReleasedEvent;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.RepositoryPlace;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

public class ModelTreePanelPresenter implements Presenter {
	private static final Logger logger = LoggerFactory.getLogger(ModelTreePanelPresenter.class.getName());
	
	interface ModelTreePanelEventBinder extends EventBinder<ModelTreePanelPresenter> {}
	private final ModelTreePanelEventBinder eventBinder = GWT.create(ModelTreePanelEventBinder.class);

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void reload();
		void setSelected(TreeItem item);
		void setOpenedAndSelected(TreeItem item);
		TreeItem getSelectedObject();
		void setSaveEnabled(boolean enabled);
		void setRemoveEnabled(boolean enabled);
		void setReleaseVersionEnabled(boolean enabled);
		void setDeployEnabled(boolean enabled);
		void setModels(List<TreeItem> modelTreeItems);
		void setVersions(long modelId, List<TreeItem> versionTreeItems);
		void setRepositories(long versionId, List<TreeItem> repoTreeItems);
	}

	private View view;
	private ModelController modelController;
	private VersionController versionController;
	private PlaceController placeController;
	private RepositoryController repositoryController;

	@Inject
	public ModelTreePanelPresenter(View view, ModelController modelController,
			VersionController versionController, RepositoryController repositoryController, 
			PlaceController placeController, EventBus eventBus) {
		this.view = view;
		this.modelController = modelController;
		this.versionController = versionController;
		this.repositoryController = repositoryController;
		this.placeController = placeController;

		eventBinder.bindEventHandlers(this, eventBus);
		view.setPresenter(this);
	}

	@Override
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public boolean isLeaf(TreeItem value) {
		return isRepository(value) || isLoading(value);
	}

	public void reload() {
		view.reload();
	}

	@Override
	public void onAddNewModel() {
		modelController.createNewModel(new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				placeController.goTo(new ModelPlace(model.getId()));
			}
		});
	}

	@Override
	public void onSelected() {
		TreeItem item = view.getSelectedObject();
		if (item != null) {
			if (isModel(item)) {
				placeController.goTo(new ModelPlace(item.getId()));
			} else if (isVersion(item)) {
				placeController.goTo(new VersionPlace(item.getId())); 
			} else if (isRepository(item)) {
				placeController.goTo(new RepositoryPlace(item.getId())); 
			} 
			// XXX other elements
		}
	}

	@Override
	public void onRemove() {
		final TreeItem item = view.getSelectedObject();
		modelController.deleteModel(item.getId(), new NextCallback() {
			@Override
			public void next() {
				view.setSelected(null);
				refreshModelList(new NextCallback() {
					@Override
					public void next() {
						placeController.goTo(new WelcomePlace());
					}
				});
			}
		});
	}

	@Override
	public void onSave() {
		final TreeItem item = view.getSelectedObject();
		if (isModel(item)) {
			modelController.saveModel(item.getId(), new ModelCallback() {
				@Override
				public void setModel(final ModelProxy model) {
					refreshModelList(new NextCallback() {
						@Override
						public void next() {
							if (item.getId() != model.getId()) {
								placeController.goTo(new ModelPlace(model
										.getId()));
							}
						}
					});
				}
			});
		}
	}
	
	@Override
	public void onReleaseVersion() {
		final TreeItem model = view.getSelectedObject();
		
		if (isModel(model))
			versionController.releaseNewVersion(model.getId(), new VersionCallback() {
				@Override
				public void setVersion(final Version version) {
					loadVersionsForModel(model.getId(), new NextCallback() {
						@Override
						public void next() {
							placeController.goTo(new VersionPlace(version.getId()));
						}
					});
				}
			});
	}

	public void setSelected(final TreeItem item) {
		if (item != null) {
			if (isModel(item)) {
				modelController.getModel(item.getId(), new ModelCallback() {
					@Override
					public void setModel(ModelProxy model) {
						view.setOpenedAndSelected(item);
						view.setRemoveEnabled(item != null);
						view.setDeployEnabled(false);
						view.setSaveEnabled(model.isDirty());
						if (item.isDirty())
							view.setReleaseVersionEnabled(false);
						else 
							view.setReleaseVersionEnabled(true);
					}
				});
			} else if (isVersion(item)) {
				versionController.getVersion(item.getId(), new VersionCallback() {
					@Override
					public void setVersion(Version version) {
						view.setOpenedAndSelected(item);
						view.setRemoveEnabled(false);
						view.setDeployEnabled(true); 
						view.setSaveEnabled(false);
						view.setReleaseVersionEnabled(false);
					}
				});
			}
			// XXX other tree elements
		} else {
			view.setOpenedAndSelected(null);
			view.setRemoveEnabled(false);
			view.setDeployEnabled(false);
			view.setSaveEnabled(false);
			view.setReleaseVersionEnabled(false);
		}
	}

	@Override
	public void loadChildren(TreeItem item) {
		logger.debug("Loading children for {}", item);
		
		if (isRoot(item)) {
			refreshModelList(null);
		} else if (isModel(item)) {
			loadVersionsForModel(item.getId(), null);
		} else if (isVersion(item)) {
			loadRepositoriesForVersion(item.getId(), null);
		}
	}
	
	@Override
	public void onDeploy() {
		final TreeItem version = view.getSelectedObject();
		
		if (isVersion(version))
			repositoryController.deployRepository(version.getId(), new RepositoryCallback() {
				@Override
				public void setRepository(final Repository repository) {
					loadRepositoriesForVersion(version.getId(), new NextCallback() {
						@Override
						public void next() {
							placeController.goTo(new RepositoryPlace(repository.getId()));
						}
					});
				}
			});
	}

	@EventHandler
	void onModelChanged(final ModelChangedEvent event) {
		refreshAndSelectModel(event.getModelId());
	}

	@EventHandler
	void onNewModel(NewModelEvent event) {
		refreshAndSelectModel(event.getModelId());
	}
	
	@EventHandler
	void onVersionReleased(final VersionReleasedEvent event) {
		loadVersionsForModel(event.getModelId(), new NextCallback() {
			@Override
			public void next() {
				setSelected(TreeItem.newVersion(event.getVersionId()));
			}
		});
	}

	private void refreshAndSelectModel(final long modelId) {
		refreshModelList(new NextCallback() {
			@Override
			public void next() {
				setSelected(TreeItem.newModel(modelId));
			}
		});
	}
	
	private void refreshModelList(final NextCallback callback) {
		modelController.getModels(new ModelsCallback() {
			@Override
			public void setModels(List<ModelProxy> models) {
				List<TreeItem> modelTreeItems = new ArrayList<TreeItem>();
				for (ModelProxy model : models) {
					TreeItem item = TreeItem.newModel(model.getId(),
							model.getName());
					item.setDirty(model.isDirty());
					modelTreeItems.add(item);
				}
				view.setModels(modelTreeItems);
				if (callback != null) {
					callback.next();
				}
			}
		}, false);
	}
	
	private void loadVersionsForModel(final long modelId, final NextCallback callback) {
		versionController.getVersions(modelId, new VersionsCallback() {
			@Override
			public void setVersions(List<Version> versions) {
				List<TreeItem> versionTreeItems = new ArrayList<TreeItem>();
				if (versions != null) 
					for (Version version : versions) {
						TreeItem item = TreeItem.newVersion(version.getId(),
								version.getName());
						versionTreeItems.add(item);
					}
				view.setVersions(modelId, versionTreeItems);
				if (callback != null) {
					callback.next();
				}
			}
		}, false);
	}
	
	private void loadRepositoriesForVersion(final long versionId, final NextCallback callback) {
		repositoryController.getRepositories(versionId, new RepositoriesCallback() {
			@Override
			public void setRepositories(List<Repository> list) {
				List<TreeItem> repoTreeItems = new ArrayList<TreeItem>();
				
				if (list != null) 
					for (Repository version : list) {
						TreeItem item = TreeItem.newRepository(version.getId(),
								version.getName());
						repoTreeItems.add(item);
					}
				view.setRepositories(versionId, repoTreeItems);
				
				if (callback != null) {
					callback.next();
				}
			}
		}, false);
	}
}