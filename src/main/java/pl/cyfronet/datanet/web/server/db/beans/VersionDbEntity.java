package pl.cyfronet.datanet.web.server.db.beans;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

@Entity
public class VersionDbEntity {
	@Id
	@GeneratedValue
	protected long id;
	protected String name; 
	@Type(type="date")
	protected Date timestamp;
	@Column(length = 17000000)
	protected String modelXml;
	@ManyToOne
	private ModelDbEntity model;
	@OneToMany
	private List<RepositoryDbEntity> repositories;

	@Override
	public String toString() {
		return "ModelVersionDbEntity [id=" + id + ", name="
				+ name + ", creationDate=" + timestamp + ", model="
				+ model + ", modelXml=" + modelXml + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ModelDbEntity getModel() {
		return model;
	}

	public void setModel(ModelDbEntity model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}
	public void setName(String versionName) {
		this.name = versionName;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getModelXml() {
		return modelXml;
	}
	public void setModelXml(String modelXml) {
		this.modelXml = modelXml;
	}
	public List<RepositoryDbEntity> getRepositories() {
		return repositories;
	}
}