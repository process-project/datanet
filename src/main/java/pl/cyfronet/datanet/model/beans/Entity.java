package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

public class Entity implements Serializable {
	private static final long serialVersionUID = 992005257282112449L;
		
	private String id;
	private String name;
	private List<Field> fields;
	
	public Entity() {
		fields = new ArrayList<Field>();
		id = UUID.uuid();
		
	}
	
	public Entity(Entity entity) {
		name = entity.getName();
		id = entity.getId();
		
		fields = new ArrayList<Field>();
		for (Field field : entity.getFields()) {
			fields.add(new Field(field));
		}
	}
	
	@XmlID
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Entity other = (Entity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Entity [id=" + id + ", name=" + name + ", fields=" + fields
				+ "]";
	}
}