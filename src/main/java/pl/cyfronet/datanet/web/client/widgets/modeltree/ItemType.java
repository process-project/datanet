package pl.cyfronet.datanet.web.client.widgets.modeltree;

public enum ItemType {
	MODEL,
	VERSION,
	REPOSITORY,
	LOADING;
	
	static boolean isRoot(TreeItem item) {
		return item == null;
	}
	
	static boolean isModel(TreeItem item) {
		return item != null && item.isOfType(MODEL);
	}
	
	static boolean isVersion(TreeItem item) {
		return item != null && item.isOfType(VERSION);
	}
	
	static boolean isRepository(TreeItem item) {
		return item != null && item.isOfType(REPOSITORY);
	}
	
	static boolean isLoading(TreeItem item) {
		return item != null && item.isOfType(LOADING);
	}
}
