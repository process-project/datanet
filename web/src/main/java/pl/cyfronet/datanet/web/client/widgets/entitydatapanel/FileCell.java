package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class FileCell extends AbstractCell<String> {
	private static final String SEPARATOR = ";";
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		if (value != null) {
			String[] elements = value.split(SEPARATOR);
			String url = elements.length > 1 ? elements[elements.length - 1] : "";
			String fileName = elements.length > 0 ? value.substring(0, value.length() - url.length() - 1) : "";			
			sb.appendHtmlConstant("<a href='download?fileUrl=" + url + "&fileName=" + fileName + "' target='_blank'>" + fileName + "</a>");
		}
	}
}