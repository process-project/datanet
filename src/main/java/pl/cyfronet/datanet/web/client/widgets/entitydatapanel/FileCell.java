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
			String fileName = elements.length > 0 ? elements[0] : "";
			String repositoryId = elements.length > 1 ? elements[1] : "";
			String fileId = elements.length > 2 ? elements[2] : "";
			SafeHtml encodedFileName = SafeHtmlUtils.fromString(fileName);
			sb.appendHtmlConstant("<a href='download?fileId=" + fileId + "&repositoryId=" + repositoryId + "&fileName=" + encodedFileName.asString() + "' target='_blank'>")
					.append(encodedFileName)
					.appendHtmlConstant("</a>");
		}
	}
}