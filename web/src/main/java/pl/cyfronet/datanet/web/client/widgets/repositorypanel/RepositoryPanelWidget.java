package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.Map;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryPanelWidget extends ResizeComposite implements View {
	private static RepositoryPanelWidgetUiBinder uiBinder = GWT.create(RepositoryPanelWidgetUiBinder.class);
	interface RepositoryPanelWidgetUiBinder extends UiBinder<Widget, RepositoryPanelWidget> {}
	interface RepositoryPanelWidgetStyles extends CssResource {}

	private Presenter presenter;

	@UiField Button repositoryLink;
	@UiField TabPanel tabPanel;
	@UiField RepositoryPanelMessages messages;
	
	public RepositoryPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setRepositoryLink(String link) {
		repositoryLink.setHref(link);
		repositoryLink.setText(messages.repositoryLocationPrefix(link));
	}

	@Override
	public void addEntity(final String entityName, IsWidget widget) {
		Tab tab = new Tab();
		tab.setHeading(entityName);
		tab.add(widget.asWidget());
		tabPanel.add(tab);
	}

	@Override
	public void showEntity(int entityIndex) {
		tabPanel.selectTab(entityIndex);
	}

	@Override
	public Map<String, String> getSearchFieldValues() {
		// TODO Auto-generated method stub
		return null;
	}
}