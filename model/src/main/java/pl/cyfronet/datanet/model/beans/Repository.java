package pl.cyfronet.datanet.model.beans;


public class Repository {
	private long id;
	private String name;
	private Model sourceModel;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Model getSourceModel() {
		return sourceModel;
	}
	public void setSourceModel(Model sourceModel) {
		this.sourceModel = sourceModel;
	}
	@Override
	public String toString() {
		return "Repository [id=" + id + ", name=" + name + ", sourceModel="
				+ sourceModel + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sourceModel == null) ? 0 : sourceModel.hashCode());
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
		Repository other = (Repository) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sourceModel == null) {
			if (other.sourceModel != null)
				return false;
		} else if (!sourceModel.equals(other.sourceModel))
			return false;
		return true;
	}
	
	
}
