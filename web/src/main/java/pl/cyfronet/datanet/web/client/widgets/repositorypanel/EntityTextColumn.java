package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import com.google.gwt.user.cellview.client.TextColumn;

public class EntityTextColumn extends TextColumn<EntityRow> {
	protected String fieldName;
	
	public EntityTextColumn(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getValue(EntityRow object) {
		return object.get(fieldName);
	}
}