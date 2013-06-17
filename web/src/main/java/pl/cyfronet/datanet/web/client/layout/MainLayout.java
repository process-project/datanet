package pl.cyfronet.datanet.web.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainLayout extends Composite {
	private static MainLayoutUiBinder uiBinder = GWT.create(MainLayoutUiBinder.class);
	interface MainLayoutUiBinder extends UiBinder<Widget, MainLayout> {}
	
	@UiField FlowPanel header;
	@UiField FlowPanel west;
	@UiField FlowPanel center;

	public MainLayout() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}