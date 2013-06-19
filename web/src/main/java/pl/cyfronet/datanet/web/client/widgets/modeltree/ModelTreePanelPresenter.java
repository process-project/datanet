package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ModelController;
import pl.cyfronet.datanet.web.client.ModelController.ModelCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.inject.Inject;

public class ModelTreePanelPresenter implements Presenter {
	interface View extends IsWidget {
		void showModels(List<TreeItem> models);
		void setPresenter(Presenter presenter);
	}

	private View view;
	private ModelController modelController;

	@Inject
	public ModelTreePanelPresenter(View view, ModelController modelController) {
		this.view = view;
		this.modelController = modelController;
		
		view.setPresenter(this);
	}

	@Override
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public boolean isLeaf(TreeItem value) {
		return value.getType() == ItemType.MODEL || value.getType() == ItemType.LOADING;
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
}
