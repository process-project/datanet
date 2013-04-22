package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Field.Type;

public class Entity implements Serializable {
	private static final long serialVersionUID = 992005257282112449L;
	
	private String name;
	private List<Field> fields;
	
	public Entity() {
		fields = new ArrayList<Field>();
		
		//there should always be an id field
		Field idField = new Field();
		idField.setName("id");
		idField.setType(Type.Id);
		idField.setRequired(true);
		fields.add(idField);
	}
	
	public Entity(Entity entity) {
		name = entity.name;
		
		fields = new ArrayList<Field>();
		for (Field field : entity.getFields()) {
			fields.add(new Field(field));
		}
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
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Entity [name=" + name + ", fields=" + fields + "]";
	}
}