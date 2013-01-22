package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Model implements Serializable {
	private static final long serialVersionUID = 7356373514701080184L;

	private String name;
	private String version;
	private List<Entity> entities;
	
	public Model() {
		entities = new ArrayList<Entity>();
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
		return "Model [name=" + name + ", version=" + version + ", entities="
				+ entities + "]";
	}
}