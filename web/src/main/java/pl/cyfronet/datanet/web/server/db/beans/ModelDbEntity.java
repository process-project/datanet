package pl.cyfronet.datanet.web.server.db.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class ModelDbEntity {
	@Id @GeneratedValue
	private long id;
	private String name;
	private String version;
	@Column(length = 17000000) //should map to longtext or equivalent
	private String experimentBody;
	@ManyToMany(cascade = {CascadeType.PERSIST})
	private List<UserDbEntity> owners;
	
	public List<UserDbEntity> getOwners() {
		return owners;
	}
	public void setOwners(List<UserDbEntity> owners) {
		this.owners = owners;
	}
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
	public String getExperimentBody() {
		return experimentBody;
	}
	public void setExperimentBody(String experimentBody) {
		this.experimentBody = experimentBody;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "ModelDbEntity [id=" + id + ", name=" + name + ", version="
				+ version + ", experimentBody=" + experimentBody + ", owners="
				+ owners + "]";
	}
}