package pl.cyfronet.datanet.web.server.db.beans;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class UserDbEntity {
	@Id @GeneratedValue
	private long id;
	private String login;
	@ManyToMany(mappedBy = "owners")
	private List<ModelDbEntity> models;
	private Date lastLogin;
	
	public List<ModelDbEntity> getModels() {
		return models;
	}
	public void setModels(List<ModelDbEntity> models) {
		this.models = models;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
}