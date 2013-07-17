package pl.cyfronet.datanet.web.client.widgets.modeltree;

public class TreeItemWrapper {

	private TreeItemWrapper child;
	private TreeItem item;
	
	private TreeItemWrapper(TreeItemWrapper child, TreeItem item) {
		super();
		this.child = child;
		this.item = item;
	}

	public TreeItemWrapper getChild() {
		return child;
	}

	public TreeItem getItem() {
		return item;
	}	
	
	public static TreeItemWrapper createFirst(TreeItem item) {
		return new TreeItemWrapper(null, item);
	}
	
	public TreeItemWrapper appendParent(TreeItem item) {
		return new TreeItemWrapper(this, item);
	}
	
}
