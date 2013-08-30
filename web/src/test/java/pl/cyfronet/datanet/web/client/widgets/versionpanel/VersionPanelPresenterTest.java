package pl.cyfronet.datanet.web.client.widgets.versionpanel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoriesCallback;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.VersionNotificationMessage;
import pl.cyfronet.datanet.web.client.mvp.place.RepositoryPlace;
import pl.cyfronet.datanet.web.client.widgets.versionpanel.VersionPanelPresenter.View;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

@RunWith(GwtMockitoTestRunner.class)
public class VersionPanelPresenterTest {
	@Mock private View view;
	@Mock private VersionPanelWidgetMessages messages;
	@Mock private RepositoryController repositoryController;
	@Mock private VersionController versionController;
	@Mock private PlaceController placeController;
	@Mock private EventBus eventBus;

	private String repositoryName;
	private VersionPanelPresenter presenter;
	protected String errorMsg = "error message";	

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		
		when(view.getEntityContainer()).thenReturn(mock(HasWidgets.class));
		presenter = new VersionPanelPresenter(view, messages, repositoryController, versionController, placeController, eventBus);
		
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
		verify(placeController).goTo(any(RepositoryPlace.class));
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
		verify(placeController, times(0)).goTo(any(RepositoryPlace.class));
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
	
	@Test
	public void shouldNotRemoveVersionAfterUserCancels() {
		when(view.confirmVersionRemoval()).thenReturn(false);
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoriesCallback repositoriesCallback = (RepositoriesCallback) invocation.getArguments()[1];
				//list is empty so only confirmation is left
				repositoriesCallback.setRepositories(new ArrayList<Repository>());
				
				return null;
			}
		}).when(repositoryController).getRepositories(Mockito.anyLong(), Mockito.any(RepositoriesCallback.class), Mockito.anyBoolean());
		
		presenter.onRemoveVersion();
		
		verify(view, never()).setRemoveVersionBusyState(Mockito.anyBoolean());
	}
	
	@Test
	public void shouldNotRemoveVersionAsRepositoriesExist() {
		when(view.confirmVersionRemoval()).thenReturn(true);
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoriesCallback repositoriesCallback = (RepositoriesCallback) invocation.getArguments()[1];
				repositoriesCallback.setRepositories(Arrays.asList(new Repository()));
				
				return null;
			}
		}).when(repositoryController).getRepositories(Mockito.anyLong(), Mockito.any(RepositoriesCallback.class), Mockito.anyBoolean());
		
		presenter.onRemoveVersion();
		
		verify(view, never()).setRemoveVersionBusyState(Mockito.anyBoolean());
		verify(eventBus).fireEvent(new NotificationEvent(
				VersionNotificationMessage.versionCannotRemoveRepositoriesExist, NotificationType.ERROR));
	}
	
	@Test
	public void shouldEnableAndDisableBusyStateWhileSuccessfullyRemovingVersion() {
		when(view.confirmVersionRemoval()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoriesCallback repositoriesCallback = (RepositoriesCallback) invocation.getArguments()[1];
				repositoriesCallback.setRepositories(new ArrayList<Repository>());
				
				return null;
			}
		}).when(repositoryController).getRepositories(Mockito.anyLong(), Mockito.any(RepositoriesCallback.class), Mockito.anyBoolean());
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Command command = (Command) invocation.getArguments()[1];
				command.execute();
				
				return null;
			}
		}).when(versionController).removeVersion(Mockito.anyLong(), Mockito.any(Command.class));;
		
		presenter.onRemoveVersion();
		
		verify(view).setRemoveVersionBusyState(true);
		verify(view).setRemoveVersionBusyState(false);
	}
}