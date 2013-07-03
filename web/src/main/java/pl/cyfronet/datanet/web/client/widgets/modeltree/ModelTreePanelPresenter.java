package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEventHandler;
import pl.cyfronet.datanet.web.client.event.model.NewModelEvent;
import pl.cyfronet.datanet.web.client.event.model.NewModelEventHandler;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ModelTreePanelPresenter implements Presenter {
	private static final Logger logger = Logger
			.getLogger(ModelTreePanelPresenter.class.getName());

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);

		void reload();

		void setSelected(TreeItem item);

		TreeItem getSelectedObject();

		void setSaveEnabled(boolean enabled);

		void setRemoveEnabled(boolean enabled);

		void setDeployEnabled(boolean enabled);

		void setModels(List<TreeItem> modelTreeItems);
	}

	private interface NextCallback {
		void next();
	}

	private View view;
	private ModelController modelController;
	private PlaceController placeController;

	@Inject
	public ModelTreePanelPresenter(View view, ModelController modelController,
			PlaceController placeController, EventBus eventBus) {
		this.view = view;
		this.modelController = modelController;
		this.placeController = placeController;
		view.setPresenter(this);

		eventBus.addHandler(ModelChangedEvent.TYPE,
				new ModelChangedEventHandler() {
					@Override
					public void onModelChangedEvent(ModelChangedEvent event) {
						modelChanged(event.getModelId());
					}
				});

		eventBus.addHandler(NewModelEvent.TYPE, new NewModelEventHandler() {
			@Override
			public void onNewModelEvent(NewModelEvent event) {
				addModel(event.getModelId());
			}
		});
	}

	private void addModel(final Long modelId) {
		refreshModelList(new NextCallback() {
			@Override
			public void next() {
				setSelected(new TreeItem(modelId, null, ItemType.MODEL));
			}
		});
	}

	private void refreshModelList(final NextCallback callback) {
		modelController.getModels(new ModelsCallback() {
			@Override
			public void setModels(List<ModelProxy> models) {
				List<TreeItem> modelTreeItems = new ArrayList<TreeItem>();
				for (ModelProxy model : models) {
					TreeItem item = new TreeItem(model.getId(),
							model.getName(), ItemType.MODEL);
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

	private void modelChanged(final Long modelId) {
		refreshModelList(new NextCallback() {
			@Override
			public void next() {
				setSelected(new TreeItem(modelId, null, ItemType.MODEL));
			}
		});
	}

	@Override
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public boolean isLeaf(TreeItem value) {
		return value.getType() == ItemType.MODEL
				|| value.getType() == ItemType.LOADING;
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
			}
			// XXX other elements
		}
	}

	private boolean isModel(TreeItem item) {
		return item.getType() == ItemType.MODEL;
	}

	@Override
	public void onRemove() {
		// TODO actually remove model
		placeController.goTo(new WelcomePlace());
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

	public void setSelected(final TreeItem item) {
		if (item != null) {
			if (isModel(item)) {
				modelController.getModel(item.getId(), new ModelCallback() {
					@Override
					public void setModel(ModelProxy model) {
						view.setSelected(item);

						boolean actionEnabled = item != null;
						view.setRemoveEnabled(actionEnabled);
						view.setDeployEnabled(!model.isNew());
						view.setSaveEnabled(model.isDirty());
					}
				});
			}
			// XXX other tree elements
		} else {
			view.setRemoveEnabled(false);
			view.setDeployEnabled(false);
			view.setSaveEnabled(false);
		}
	}

	@Override
	public void loadChildren(TreeItem parent) {
		logger.log(Level.INFO, "Loading children for " + parent);
		if (parent == null) {
			refreshModelList(null);
		}
	}
}
