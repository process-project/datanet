package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryPanelWidget extends ResizeComposite implements View {

	private static RepositoryPanelWidgetUiBinder uiBinder = GWT
			.create(RepositoryPanelWidgetUiBinder.class);

	interface RepositoryPanelWidgetUiBinder extends
			UiBinder<Widget, RepositoryPanelWidget> {
	}

	interface RepositoryPanelWidgetStyles extends CssResource {
	}

	private Presenter presenter;

	@UiField TextBox repositoryName;
	@UiField Panel repositoryContainer;
	@UiField Anchor repositoryLink;
	
	public RepositoryPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public HasWidgets getRepositoryContainer() {
		return repositoryContainer;
	}

	public void setRepositoryName(String name) {
		repositoryName.setText(name);
	}

	@Override
	public void setRepositoryLink(String link) {
		repositoryLink.setHref(link);
	}
}