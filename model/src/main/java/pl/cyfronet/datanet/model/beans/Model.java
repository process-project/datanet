package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Model implements Serializable {
	private static final long serialVersionUID = 7356373514701080184L;

	private long id;
	private String name;
	private String version;
	private List<Entity> entities;
	
	public Model() {
		entities = new ArrayList<Entity>();
	}
	
	public Model(Model model) {
		id = model.getId();
		name = model.getName();
		version = model.version;
		entities = new ArrayList<Entity>();
		for(Entity entity : model.getEntities()) {
			entities.add(new Entity(entity));
		}
		
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
	
	@XmlAttribute
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	@Override
	public String toString() {
		return "Model [id=" + id + ", name=" + name + ", version=" + version
				+ ", entities=" + entities + "]";
	}
}