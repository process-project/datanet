package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public class EntityActionColumn extends Column<EntityRow, String> {
	public EntityActionColumn(Cell<String> cell) {
		super(cell);
	}

	@Override
	public String getValue(EntityRow object) {
		return object.get("id");
	}
}