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
		Boolean, BooleanArray
	}
	
	private String name;
	private Type type;
	
	public Field() {
		type = Type.Id;
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
	
	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}
}