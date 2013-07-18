package pl.cyfronet.datanet.web.server.db.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity
public class RepositoryDbEntity {
	@Id
	@GeneratedValue
	private long id;
	private String name;
	@OneToOne
	private VersionDbEntity sourceModelVersion;
	@ManyToMany(cascade = { CascadeType.PERSIST })
	private List<UserDbEntity> owners;

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

	public VersionDbEntity getSourceModelVersion() {
		return sourceModelVersion;
	}

	public void setSourceModelVersion(VersionDbEntity sourceModelVersion) {
		this.sourceModelVersion = sourceModelVersion;
	}

	public List<UserDbEntity> getOwners() {
		return owners;
	}

	public void setOwners(List<UserDbEntity> owners) {
		this.owners = owners;
	}

	@Override
	public String toString() {
		return "RepositoryDbEntity [id=" + id + ", name=" + name
				+ ", sourceModelVersion=" + sourceModelVersion + ", owners="
				+ owners + "]";
	}
}