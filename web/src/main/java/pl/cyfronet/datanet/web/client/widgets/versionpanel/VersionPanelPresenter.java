package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.widgets.readonly.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.readonly.entitypanel.EntityPanelWidget;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

public class VersionPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setModelName(String name);

		HasWidgets getEntityContainer();

		void setDeployError(String errorMsg);

		void hideDeployModal();

		void setPresenter(Presenter presenter);
	}

	private View view;
	private VersionPanelWidgetMessages messages;
	private RepositoryController repositoryController;
	private Version version;

	@Inject
	public VersionPanelPresenter(View view,
			VersionPanelWidgetMessages messages,
			RepositoryController repositoryController) {
		this.view = view;
		this.messages = messages;
		this.repositoryController = repositoryController;
		view.setPresenter(this);
	}

	@Override
	public IsWidget getWidget() {
		return view;
	}

	public void setVersion(Version version) {
		this.version = version;
		view.setModelName(version.getName());
		view.getEntityContainer().clear();

		if (version.getEntities() != null) {
			for (Entity entity : version.getEntities()) {
				displayEntity(entity);
			}
		}
	}

	private void displayEntity(Entity entity) {
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(
				new EntityPanelWidget());
		entityPanelPresenter.setEntity(entity);
		view.getEntityContainer().add(
				entityPanelPresenter.getWidget().asWidget());
	}

	@Override
	public void deploy(String repositoryName) {
		if (empty(repositoryName)) {
			view.setDeployError(messages.emptyNameError());
		} else if(unvalidRepositoryNameFormat(repositoryName)) {
			view.setDeployError(messages.wrongNameFormat());
		}else {
			deployRepository(repositoryName.toLowerCase());
		}
	}

	private boolean unvalidRepositoryNameFormat(String repositoryName) {		
		return !repositoryName.matches("[a-zA-z\\d]*");
	}

	private void deployRepository(String repositoryName) {
		repositoryController.deployRepository(version.getId(), repositoryName,
				new RepositoryCallback() {
					@Override
					public void setRepository(final Repository repository) {
						view.hideDeployModal();
					}

					@Override
					public void setError(String errorMsg) {
						view.setDeployError(errorMsg);
					}
				});
	}

	private boolean empty(String repositoryName) {
		return repositoryName == null || repositoryName.equals("");
	}
}
