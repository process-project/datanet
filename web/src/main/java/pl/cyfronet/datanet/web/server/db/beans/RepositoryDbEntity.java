package pl.cyfronet.datanet.web.server.db.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
	private ModelDbEntity sourceModel;
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

	public ModelDbEntity getSourceModel() {
		return sourceModel;
	}

	public void setSourceModel(ModelDbEntity sourceModel) {
		this.sourceModel = sourceModel;
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
				+ ", sourceModel=" + sourceModel + ", owners=" + owners + "]";
	}
}