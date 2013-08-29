package pl.cyfronet.datanet.web.client.widgets.modeltree;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	interface TreeItemCallback { 
		void onTreeItemProvided(TreeItem item);
	}
	
	IsWidget getWidget();
	boolean isLeaf(TreeItem value);
	void onAddNewModel();
	void onSelected();
	void loadChildren(TreeItem parent);
	void getParent(TreeItem item, TreeItemCallback treeItemCallback);
}
