package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter.View;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

@RunWith(GwtMockitoTestRunner.class)
public class VersionPanelPresenterTest {
	@Mock private View view;
	@Mock private VersionPanelWidgetMessages messages;
	@Mock private RepositoryController repositoryController;
	@Mock private VersionController versionController;
	@Mock private EventBus eventBus;

	private String repositoryName;
	private VersionPanelPresenter presenter;
	protected String errorMsg = "error message";

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		
		when(view.getEntityContainer()).thenReturn(mock(HasWidgets.class));
		presenter = new VersionPanelPresenter(view, messages, repositoryController, versionController, eventBus);
		
		Version version = new Version();
		version.setId(1l);
		presenter.setVersion(version);
				
	}
	
	@Test
	public void shouldDeployRepository() throws Exception {
		 givenUniqueRepositoryName();
		 whenDeployRepository();
		 thenRepositoryDeployed();
	}

	private void givenUniqueRepositoryName() {
		repositoryName = "repositoryname";
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoryCallback callback = (RepositoryCallback) invocation.getArguments()[2];
				callback.setRepository(new Repository());
				return null;
			}
		}).when(repositoryController).deployRepository(eq(1l), eq(repositoryName), any(RepositoryCallback.class));
	}

	private void whenDeployRepository() {
		presenter.deploy(repositoryName );
	}

	private void thenRepositoryDeployed() {
		verify(view).hideDeployModal();
	}
	
	@Test
	public void shouldShowErrorOnDeployError() throws Exception {
		 givenNonUniqueRepositoryName();
		 whenDeployRepository();
		 thenErrorPresented();
	}

	private void givenNonUniqueRepositoryName() {
		repositoryName = "repositoryname";
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoryCallback callback = (RepositoryCallback) invocation.getArguments()[2];
				callback.setError(errorMsg );
				return null;
			}
		}).when(repositoryController).deployRepository(eq(1l), eq(repositoryName), any(RepositoryCallback.class));
	}

	private void thenErrorPresented() {
		verify(view).setDeployError(errorMsg);
	}
	
	@Test
	public void shouldShowErorrOnEmptyRepositoryName() throws Exception {
		 givenEmptyRepositoryName();
		 whenDeployRepository();
		 thenErrorPresented();
	}

	private void givenEmptyRepositoryName() {
		repositoryName = "";
		when(messages.emptyNameError()).thenReturn(errorMsg);
	}
	
	@Test
	public void shouldShowErrorWhenNameFormatIsNotCorrect() throws Exception {
		 givenWrongFormatForRepositoryName();
		 whenDeployRepository();
		 thenErrorPresented();
	}

	private void givenWrongFormatForRepositoryName() {
		repositoryName = "wrong repository name !@#";
		when(messages.wrongNameFormat()).thenReturn(errorMsg);
	}
}
