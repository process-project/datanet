package pl.cyfronet.datanet.web.client.widgets.modeltree;

public class TreeItem {
	private String name;
	private String id;
	private ItemType type;

	public TreeItem(String id, String name, ItemType type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public ItemType getType() {
		return type;
	}
}
