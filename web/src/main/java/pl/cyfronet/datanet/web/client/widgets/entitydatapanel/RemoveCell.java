package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class RemoveCell extends ActionCell<String> {
	private SafeHtml message;

	public RemoveCell(final SafeHtml message, final Presenter presenter) {
		super(message, new Delegate<String>() {
			@Override
			public void execute(String object) {
				presenter.onRemoveRow(object);
			}
		});
		this.message = message;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<button class='btn btn-mini btn-danger'>")
			.append(message)
			.appendHtmlConstant("</button>");
	}
}