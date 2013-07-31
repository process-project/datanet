package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlIDREF;

public class Field implements Serializable {
	private static final long serialVersionUID = 7956535573658372420L;

	public enum Type {
		ObjectId, ObjectIdArray,

		String, StringArray,

		Integer, IntegerArray,

		Float, FloatArray,

		Boolean, BooleanArray,

		File;

		private static final String ARRAY = "Array";
		private static final String TAB = "[]";

		public String typeName() {
			String name = super.name();
			if (name.endsWith(ARRAY)) {
				// String.format cannot be used because of GWT
				return substring(name, ARRAY) + TAB;
			}

			return name;
		}

		public static Type typeValueOf(String typeString) {
			if (typeString.endsWith(TAB)) {
				try {
					return valueOf(substring(typeString, TAB) + ARRAY);
				} catch (IllegalArgumentException e) {
					return ObjectIdArray;
				}
			}
			try {
				return valueOf(typeString);
			} catch (IllegalArgumentException e) {
				return ObjectId;
			}
		}

		private static String substring(String str, String toRemove) {
			return str.substring(0, str.length() - toRemove.length());
		}
	}

	private String name;
	private Type type;
	private boolean required;

	/**
	 * Relation target. This field is taken into account only when type is
	 * ObjectId or ObjectIdArray.
	 */
	private Entity target;

	public Field() {
		type = Type.String;
		required = true;
	}

	public Field(Field field) {
		name = field.getName();
		type = field.getType();
		required = field.isRequired();
		target = field.getTarget();
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

	@XmlIDREF
	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + ", required="
				+ required + ", target=" + target + "]";
	}
}