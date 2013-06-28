package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ModelController;
import pl.cyfronet.datanet.web.client.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEventHandler;
import pl.cyfronet.datanet.web.client.event.model.ModelSavedEvent;
import pl.cyfronet.datanet.web.client.event.model.ModelSavedEventHandler;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.NewModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class ModelTreePanelPresenter implements Presenter {
	private static final Logger logger = Logger
			.getLogger(ModelTreePanelPresenter.class.getName());

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);

		void reload();

		void setSelected(TreeItem item);

		void updateTreeItem(TreeItem item);

		TreeItem getSelectedObject();

		void setSaveEnabled(boolean enabled);

		void setRemoveEnabled(boolean enabled);

		void setDeployEnabled(boolean enabled);
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
						GWT.log("Model changed " + event.getModelId());
						modelChanged(event.getModelId(), true);
					}
				});
		eventBus.addHandler(ModelSavedEvent.TYPE, new ModelSavedEventHandler() {
			@Override
			public void onModelSavedEvent(ModelSavedEvent event) {
				modelChanged(event.getModelId(), false);
			}
		});
	}

	private void modelChanged(final Long modelId, final boolean dirty) {
		modelController.getModel(modelId, new ModelCallback() {
			@Override
			public void setModel(Model model) {
				TreeItem item = new TreeItem(model.getId(), model.getName(),
						ItemType.MODEL);
				item.setDirty(dirty);
				view.updateTreeItem(item);
				enableSaveIfDirtyAndSelected(item);
			}
		});
	}

	private void enableSaveIfDirtyAndSelected(TreeItem item) {
		TreeItem selectedItem = view.getSelectedObject();
		if (item.equals(selectedItem)) {
			view.setSaveEnabled(item.isDirty());
		}
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

	@Override
	public void loadChildren(TreeItem parent,
			final AsyncDataProvider<TreeItem> dataProvider) {
		logger.log(Level.INFO, "Loading children for " + parent);
		modelController.getModels(new ModelsCallback() {
			@Override
			public void setModels(List<Model> models) {
				List<TreeItem> modelTreeItems = new ArrayList<TreeItem>();
				for (Model model : models) {
					modelTreeItems.add(new TreeItem(model.getId(), model
							.getName(), ItemType.MODEL));
				}
				dataProvider.updateRowCount(modelTreeItems.size(), true);
				dataProvider.updateRowData(0, modelTreeItems);
			}
		}, false);
	}

	public void reload() {
		view.reload();
	}

	@Override
	public void onAddNewModel() {
		placeController.goTo(new NewModelPlace());
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
		TreeItem item = view.getSelectedObject();
		if (isModel(item)) {
			modelController.saveModel(item.getId());
		}
	}

	public void setSelected(TreeItem item) {
		view.setSelected(item);

		boolean actionEnabled = item != null;
		view.setRemoveEnabled(actionEnabled);
		view.setDeployEnabled(actionEnabled);
	}
}
