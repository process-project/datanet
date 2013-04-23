package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;

public class Field implements Serializable {
	private static final long serialVersionUID = 7956535573658372420L;
	
	public enum Type {
		Id,
		ObjectId, ObjectIdArray,
		String, StringArray,
		Integer, IntegerArray,
		Float, FloatArray,
		Boolean, BooleanArray,
		File
	}
	
	private String name;
	private Type type;
	private boolean required;
	
	public Field() {
		type = Type.Id;
		required = true;
	}
	
	public Field(Field field) {
		name = field.getName();
		type = field.getType();
		required = field.isRequired();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Field other = (Field) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (required != other.required)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + ", required="
				+ required + "]";
	}
}