package pl.cyfronet.datanet.web.client.widgets.modeltree;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	IsWidget getWidget();
	boolean isLeaf(TreeItem value);
	void onAddNewModel();
	void onSave();
	void onReleaseVersion();
	void onRemove();
	void onSelected();
	void loadChildren(TreeItem parent);
	void onDeploy();
		
	public void getParent(TreeItem item, TreeItemCallback treeItemCallback);

	public interface TreeItemCallback { 
		void onTreeItemProvided(TreeItem item);
	}
	
}
