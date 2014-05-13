package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class FileCell extends AbstractCell<String> {
	private static final String SEPARATOR = ";";
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		if (value != null) {
			String[] elements = value.split(SEPARATOR);
			String url = elements.length > 1 ? elements[elements.length - 1] : "";
			String fileName = elements.length > 0 ? value.substring(0, value.length() - url.length() - 1) : "";			
			SafeHtml encodedFileName = SafeHtmlUtils.fromString(fileName);
			sb.appendHtmlConstant("<a href='download?fileUrl=" + url + "&fileName=" + encodedFileName.asString() + "' target='_blank'>")
					.append(encodedFileName)
					.appendHtmlConstant("</a>");
		}
	}
}