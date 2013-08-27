package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class FileCell extends AbstractCell<String> {
	private static final String SEPARATOR = ";";
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		if (value != null) {
			String fileName = value.split(SEPARATOR).length > 0 ? value.split(SEPARATOR)[0] : "";
			String url = value.split(SEPARATOR).length > 1 ? value.split(SEPARATOR)[1] : "";
			sb.appendHtmlConstant("<a href='" + url + "' target='_blank'>" + fileName + "</a>");
		}
	}
}