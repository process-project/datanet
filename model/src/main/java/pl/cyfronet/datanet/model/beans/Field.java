package pl.cyfronet.datanet.model.beans;

public class Field {
	public enum Type {
		String,
		Integer,
		Float,
		Boolean
	}
	
	private String name;
	private Type type;
	
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
}