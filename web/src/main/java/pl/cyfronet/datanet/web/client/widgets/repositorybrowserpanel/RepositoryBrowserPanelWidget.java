package pl.cyfronet.datanet.web.client.widgets.repositorybrowserpanel;

import pl.cyfronet.datanet.model.beans.Repository;
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
		private long repositoryId;

		public long getRepositoryId() {
			return repositoryId;
		}

		public void setRepositoryId(long repositoryId) {
			this.repositoryId = repositoryId;
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
	public void addRepository(Repository repository) {
		final long repositoryId = repository.getId();
		
		RepositoryLabel repositorylabel = new RepositoryLabel();
		repositorylabel.setText(repository.getName());
		repositorylabel.setRepositoryId(repositoryId);
		repositorylabel.setStyleName(style.repositoryLabel(), true);
		repositorylabel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.onRepositoryClicked(repositoryId);
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

	private RepositoryLabel getRepositoryLabelByRepositoryId(long repositoryId) {
		for (int i = 0; i < repositoryListContainer.getWidgetCount(); i++) {
			Widget w = repositoryListContainer.getWidget(i);
			if (w instanceof RepositoryLabel) {
				RepositoryLabel repositoryLabel = (RepositoryLabel) w;
				if (repositoryLabel.getRepositoryId() == repositoryId) {
					return repositoryLabel;
				}
			}
		}
		return null;
	}

	@Override
	public void markRepository(long repositoryId) {
		unmarkRepository();
		RepositoryLabel activeRepository = getRepositoryLabelByRepositoryId(repositoryId);
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