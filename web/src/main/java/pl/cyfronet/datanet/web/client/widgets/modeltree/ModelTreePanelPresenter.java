package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.ModelController;
import pl.cyfronet.datanet.web.client.ModelController.ModelCallback;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ModelTreePanelPresenter implements Presenter {
	interface View extends IsWidget {
		void showModels(List<TreeItem> models);
	}

	private View view;
	private ModelController modelController;

	@Inject
	public ModelTreePanelPresenter(View view, ModelController modelController) {
		this.view = view;
		this.modelController = modelController;
		loadModels();
	}

	private void loadModels() {
		modelController.loadModels(new ModelCallback() {
			@Override
			public void setModels(List<Model> models) {
				List<TreeItem> treeItems = new ArrayList<TreeItem>();
				for (Model model : models) {
					TreeItem item = new TreeItem(String.valueOf(model.getId()),
							model.getName(), ItemType.MODEL);
					treeItems.add(item);
				}
				view.showModels(treeItems);
			}
		});
	}

	@Override
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public List<TreeItem> getModels() {
		// List<Model> models = modelControler.getModels();
		List<TreeItem> modelTreeItems = new ArrayList<TreeItem>();
		return modelTreeItems;
	}
}
