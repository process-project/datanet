package pl.cyfronet.datanet.web.client.widgets.modeltree;

import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isModel;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isRoot;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isVersion;

import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreeViewModel.TreeItemsAsyncDataProvider;
import pl.cyfronet.datanet.web.client.widgets.modeltree.Presenter.TreeItemCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ModelTreePanel extends Composite implements View {
	private static ModelTreePanelUiBinder uiBinder = GWT.create(ModelTreePanelUiBinder.class);
	interface ModelTreePanelUiBinder extends UiBinder<Widget, ModelTreePanel> {}

	@UiField(provided = true) CellTree modelsTree;

	private ModelTreeViewModel model;
	private ModelTreePanelMessageses messages;
	private Presenter presenter;
	private SingleSelectionModel<TreeItem> selection;

	public ModelTreePanel() {
		initTree();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initTree() {
		messages = GWT.create(ModelTreePanelMessageses.class);
		selection = new SingleSelectionModel<TreeItem>();
		selection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				presenter.onSelected();
			}
		});

		model = new ModelTreeViewModel(messages, selection);
		modelsTree = new CellTree(model, null);
	}

	@UiHandler("add")
	void onAddNewModel(ClickEvent event) {
		presenter.onAddNewModel();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		model.setPresenter(presenter);
	}

	@Override
	public void reload() {
		model.reload();
	}

	@Override
	public void setSelected(final TreeItem item) {
		if (!isRoot(item)) {
			presenter.getParent(item, new TreeItemCallback() {
				@Override
				public void onTreeItemProvided(final TreeItem parent) {
					if (isModel(parent)) {
						expandTree(modelsTree.getRootTreeNode(), parent);
					} else if (isVersion(parent)) {
						openVersion(parent);						
					}
					selection.setSelected(item, true);
				}				
			});
		}
	}

	private void openVersion(final TreeItem parent) {
		presenter.getParent(parent, new TreeItemCallback() {
			@Override
			public void onTreeItemProvided(TreeItem model) {
				TreeNode modelTreeNode = expandTree(modelsTree.getRootTreeNode(), model);
				if(modelTreeNode != null) {
					expandTree(modelTreeNode, parent);
				}
			}
		});					
	}
	
	private TreeNode expandTree(TreeNode node, TreeItem item) {
		Integer childIndex = getChildIndex(node, item);
		
		if (childIndex != null) {
			return node.setChildOpen(childIndex, true);			
		}
		return null;
	}
	
	private Integer getChildIndex(TreeNode treeNode, TreeItem item) {
		for (int i = 0; i < treeNode.getChildCount(); i++) {
			if (item.equals(treeNode.getChildValue(i))) {
				return i;
			}
		}
		return null;
	}

	@Override
	public TreeItem getSelectedObject() {
		return selection.getSelectedObject();
	}

	@Override
	public void setModels(List<TreeItem> modelTreeItems) {
		model.getModelProvider().updateRowCount(modelTreeItems.size(), true);
		model.getModelProvider().updateRowData(0, modelTreeItems);
	}

	@Override
	public void setVersions(long modelId, List<TreeItem> versionTreeItems) {
		TreeItemsAsyncDataProvider versionProvider = model.getVersionProvider(modelId);
		versionProvider.updateRowCount(versionTreeItems.size(), true);
		versionProvider.updateRowData(0, versionTreeItems);
	}

	@Override
	public void setRepositories(long versionId, List<TreeItem> repoTreeItems) {
		TreeItemsAsyncDataProvider reposotoriesProvider = model.getRepositoryProvider(versionId);
		reposotoriesProvider.updateRowCount(repoTreeItems.size(), true);
		reposotoriesProvider.updateRowData(0, repoTreeItems);
	}
}
