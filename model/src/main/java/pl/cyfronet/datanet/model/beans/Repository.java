package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;


public class Repository implements Serializable {
	private static final long serialVersionUID = 6983667746241352161L;
	
	private long id;
	private String name;
	private Version sourceModelVersion;
	private String url;
	
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
	public Version getSourceModelVersion() {
		return sourceModelVersion;
	}
	public void setSourceModelVersion(Version sourceModelVersion) {
		this.sourceModelVersion = sourceModelVersion;
	}
	@Override
	public String toString() {
		return "Repository [id=" + id + ", name=" + name
				+ ", sourceModelVersion=" + sourceModelVersion + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((sourceModelVersion == null) ? 0 : sourceModelVersion
						.hashCode());
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
		if (sourceModelVersion == null) {
			if (other.sourceModelVersion != null)
				return false;
		} else if (!sourceModelVersion.equals(other.sourceModelVersion))
			return false;
		return true;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}	
}