package pl.cyfronet.datanet.model.beans;


public enum Type {
	ObjectId, ObjectIdArray,
	String, StringArray,
	Integer, IntegerArray,
	Float, FloatArray,
	Boolean, BooleanArray,
	File;

	static final String ARRAY = "Array";
	static final String TAB = "[]";

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