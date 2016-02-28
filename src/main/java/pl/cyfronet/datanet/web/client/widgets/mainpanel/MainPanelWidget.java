package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.widgets.mainpanel.MainPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelWidget extends Composite implements View {
	private static MainPanelWidgetUiBinder uiBinder = GWT.create(MainPanelWidgetUiBinder.class);
	interface MainPanelWidgetUiBinder extends UiBinder<Widget, MainPanelWidget> {}
	
	private Presenter presenter;
	
	@UiField Panel modelsPanel;
	@UiField Panel repositoriesPanel;
	@UiField Panel topNavPanel;
	
	public MainPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public void setTopNav(IsWidget widget) {
		topNavPanel.add(widget);
	}
	
	@Override
	public void setModelBrowser(IsWidget widget) {
		modelsPanel.add(widget);
	}

	@Override
	public void setRepositoryBrowser(IsWidget widget) {
		repositoriesPanel.add(widget);
	}
}