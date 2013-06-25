package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ModelController;
import pl.cyfronet.datanet.web.client.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;
import pl.cyfronet.datanet.web.client.mvp.place.NewModelPlace;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.inject.Inject;

public class ModelTreePanelPresenter implements Presenter {
	private static final Logger logger = Logger
			.getLogger(ModelTreePanelPresenter.class.getName());

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void reload();
		void setSelected(TreeItem item);
	}

	private View view;
	private ModelController modelController;
	private PlaceController placeController;

	@Inject
	public ModelTreePanelPresenter(View view, ModelController modelController,
			PlaceController placeController) {
		this.view = view;
		this.modelController = modelController;
		this.placeController = placeController;
		view.setPresenter(this);		
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
		modelController.loadModels(new ModelsCallback() {
			@Override
			public void setModels(List<Model> models) {
				List<TreeItem> modelTreeItems = new ArrayList<TreeItem>();
				for (Model model : models) {
					modelTreeItems.add(new TreeItem(String.valueOf(model
							.getId()), model.getName(), ItemType.MODEL));
				}
				dataProvider.updateRowCount(modelTreeItems.size(), true);
				dataProvider.updateRowData(0, modelTreeItems);
			}
		});
	}

	public void reload() {
		view.reload();
	}
	
	@Override
	public void onAddNewModel() {
		placeController.goTo(new NewModelPlace());
	}

	@Override
	public void onModelSelected(String modelId) {
		placeController.goTo(new ModelPlace(modelId));
	}

	public void setSelected(String modelId, ItemType type) {
		view.setSelected(new TreeItem(modelId, null, type));
	}
}
