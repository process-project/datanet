package pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel;

import pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel.RepositoryBrowserPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryBrowserPanelWidget extends Composite implements View {
	private static RepositoryBrowserPanelWidgetUiBinder uiBinder = GWT
			.create(RepositoryBrowserPanelWidgetUiBinder.class);

	interface RepositoryBrowserPanelWidgetUiBinder extends
			UiBinder<Widget, RepositoryBrowserPanelWidget> {
	}

	private Presenter presenter;
	private RepositoryBrowserPanelMessages messages;

	@UiField
	FlowPanel repositoryListContainer;
	@UiField
	Panel repositoryContainer;
	@UiField
	RepositoryBrowserPanelWidgetStyles style;
	
	interface RepositoryBrowserPanelWidgetStyles extends CssResource {
		String modelLabel();
		String marked();
	}

	public RepositoryBrowserPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = GWT.create(RepositoryBrowserPanelMessages.class);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}


	@UiHandler("undeployRepository")
	void undeployModelClicked(ClickEvent event) {
		presenter.onUndeployRepository();
	}

	@Override
	public void displayNoRepositoriesLabel() {
		repositoryListContainer.add(new Label(messages.noRepositories()));
	}

	@Override
	public void clearRepositories() {
		repositoryListContainer.clear();
	}

	@Override
	public void addRepository(String repositoryName) {
		repositoryListContainer.add(new Label(repositoryName));
	}

	@Override
	public void clearRepository() {
		repositoryContainer.clear();
	}

	@Override
	public void setRepositoryPanel(IsWidget widget) {
		clearRepository();
		repositoryContainer.add(widget);
	}

}