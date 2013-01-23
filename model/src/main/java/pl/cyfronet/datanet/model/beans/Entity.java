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
		fields.add(idField);
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
	public String toString() {
		return "Entity [name=" + name + ", fields=" + fields + "]";
	}
}