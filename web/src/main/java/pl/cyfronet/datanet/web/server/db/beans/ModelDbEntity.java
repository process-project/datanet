package pl.cyfronet.datanet.web.server.db.beans;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

@Entity
public class ModelDbEntity {
	
	private String name;
	
	@Id @GeneratedValue
	protected long id;
	
	@Type(type="date")
	protected Date timestamp;
	
	@Column(length = 17000000) 
	protected String modelXml;
		
	public ModelDbEntity() {
		super();
	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<VersionDbEntity> versions;
	
	@ManyToMany(cascade = {CascadeType.PERSIST})
	private List<UserDbEntity> owners;
	
	public List<UserDbEntity> getOwners() {
		return owners;
	}
	public void setOwners(List<UserDbEntity> owners) {
		this.owners = owners;
	}
	public List<VersionDbEntity> getVersions() {
		return versions;
	}
	public void setVersions(List<VersionDbEntity> versions) {
		this.versions = versions;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "ModelDbEntity [name=" + name + ", id=" + id + ", timestamp="
				+ timestamp + ", modelXml=" + modelXml + ", versions="
				+ versions + ", owners=" + owners + "]";
	}
	
}