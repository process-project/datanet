package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ModelController;
import pl.cyfronet.datanet.web.client.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.mvp.place.ModelPlace;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.inject.Inject;

public class ModelTreePanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
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
		modelController.loadModels(new ModelCallback() {
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

	@Override
	public void onAddNewModel() {
		placeController.goTo(new ModelPlace(null));
	}
}
