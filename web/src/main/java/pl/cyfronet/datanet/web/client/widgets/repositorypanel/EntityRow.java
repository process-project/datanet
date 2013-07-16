package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.Map;

public class EntityRow {
	private Map<String, String> fieldValues;
	
	public EntityRow(Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}
	
	public String get(String fieldName) {
		return fieldValues.get(fieldName);
	}
}