package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public class EntityFileColumn extends Column<EntityRow, String> {
	private String fieldName;
	
	public EntityFileColumn(Cell<String> cell, String fieldName) {
		super(cell);
		this.fieldName = fieldName;
	}

	@Override
	public String getValue(EntityRow entityRow) {
		return entityRow.get(fieldName);
	}
}