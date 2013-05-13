package pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel;

import pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel.RepositoryBrowserPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	private class RepositoryLabel extends Label {
		private String repositoryName;

		public String getRepositoryName() {
			return repositoryName;
		}

		public void setRepositoryName(String repositoryName) {
			this.repositoryName = repositoryName;
		}
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
		String repositoryLabel();
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
	void undeployRepositoryClicked(ClickEvent event) {
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
	public void addRepository(final String repositoryName) {
		RepositoryLabel repositorylabel = new RepositoryLabel();
		repositorylabel.setText(repositoryName);
		repositorylabel.setRepositoryName(repositoryName);
		repositorylabel.setStyleName(style.repositoryLabel(), true);
		repositorylabel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.onRepositoryClicked(repositoryName);
			}
			
		});
		repositoryListContainer.add(repositorylabel);
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

	private RepositoryLabel getRepositoryLabelByRepositoryName(String repositoryName) {
		for (int i = 0; i < repositoryListContainer.getWidgetCount(); i++) {
			Widget w = repositoryListContainer.getWidget(i);
			if (w instanceof RepositoryLabel) {
				RepositoryLabel repositoryLabel = (RepositoryLabel) w;
				if (repositoryLabel.getRepositoryName().equals(repositoryName)) {
					return repositoryLabel;
				}
			}
		}
		return null;
	}

	@Override
	public void markRepository(String repositoryName) {
		unmarkRepository();
		RepositoryLabel activeRepository = getRepositoryLabelByRepositoryName(repositoryName);
		if (activeRepository != null ) {
			activeRepository.setStyleName(style.marked(), true);
		}
	}

	@Override
	public void unmarkRepository() {
		for (int i = 0; i < repositoryListContainer.getWidgetCount(); i++) {
			repositoryListContainer.getWidget(i)
					.removeStyleName(style.marked());
		}
	}
}