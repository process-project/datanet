package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

public class ModelTreePanel extends Composite implements View {
	private static ModelTreePanelUiBinder uiBinder = GWT
			.create(ModelTreePanelUiBinder.class);

	interface ModelTreePanelUiBinder extends UiBinder<Widget, ModelTreePanel> {
	}

	@UiField(provided = true)
	CellTree modelsTree;

	@UiField
	HorizontalPanel buttons;

	private Presenter presenter;
	
	public ModelTreePanel() {
		initTree();
		initWidget(uiBinder.createAndBindUi(this));	
	}

	private void initTree() {
		TreeViewModel model = new ModelTreeModel();
		modelsTree = new CellTree(model, "root");			
	}

	/**
	 * The model that defines the nodes in the tree.
	 */
	private static class ModelTreeModel implements TreeViewModel {

		/**
		 * Get the {@link NodeInfo} that provides the children of the specified
		 * value.
		 */
		public <T> NodeInfo<?> getNodeInfo(T value) {
			/*
			 * Create some data in a data provider. Use the parent value as a
			 * prefix for the next level.
			 */
			ListDataProvider<String> dataProvider = new ListDataProvider<String>();
			for (int i = 0; i < 2; i++) {
				dataProvider.getList().add(value + "." + String.valueOf(i));
			}

			// Return a node info that pairs the data with a cell.
			return new DefaultNodeInfo<String>(dataProvider, new TextCell());
		}

		/**
		 * Check if the specified value represents a leaf node. Leaf nodes
		 * cannot be opened.
		 */
		public boolean isLeaf(Object value) {
			// The maximum length of a value is ten characters.
			return value.toString().length() > 10;
		}
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showModels(List<TreeItem> models) {
		
	}
}
