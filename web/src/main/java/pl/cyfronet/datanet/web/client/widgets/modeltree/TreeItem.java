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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeItem other = (TreeItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TreeItem [name=" + name + ", id=" + id + ", type=" + type + "]";
	}
}
