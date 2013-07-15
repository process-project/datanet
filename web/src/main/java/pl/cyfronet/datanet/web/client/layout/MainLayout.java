package pl.cyfronet.datanet.web.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainLayout extends ResizeComposite {
	private static MainLayoutUiBinder uiBinder = GWT.create(MainLayoutUiBinder.class);
	interface MainLayoutUiBinder extends UiBinder<Widget, MainLayout> {}
	
	@UiField SimpleLayoutPanel header;
	@UiField SimpleLayoutPanel west;
	@UiField SimpleLayoutPanel center;

	public MainLayout() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public AcceptsOneWidget getHeaderContainer() {
		return header;
	}
	
	public AcceptsOneWidget getWestContainer() {
		return west;
	}
	
	public AcceptsOneWidget getCenterContainer() {
		return center;
	}
}