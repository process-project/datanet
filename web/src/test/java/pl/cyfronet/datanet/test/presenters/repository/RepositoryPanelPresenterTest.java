package pl.cyfronet.datanet.test.presenters.repository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.di.factory.EntityDataPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.event.repository.RepositoryRemovedEvent;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.View;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;

public class RepositoryPanelPresenterTest {
	@Mock private View view;
	@Mock private RepositoryController repositoryController;
	@Mock private EntityDataPanelPresenterFactory entityDataPanelPresenterFactory;
	@Mock private PlaceController placeController;
	@Mock private EventBus eventBus;
	
	private RepositoryPanelPresenter repositoryPanelPresenter;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		repositoryPanelPresenter = new RepositoryPanelPresenter(
				view, repositoryController, entityDataPanelPresenterFactory, placeController, eventBus);
	}
	
	@Test
	public void testRepositoryConfirmation() {
		when(view.confirmRepositoryRemoval()).thenReturn(false);
		
		repositoryPanelPresenter.onRemoveRepository();
		
		verify(repositoryController, never()).
				removeRepository(anyLong(), anyLong(), any(Command.class), any(Command.class));
	}
	
	@Test
	public void testRepositoryRemovalEvent() {
		when(view.confirmRepositoryRemoval()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoryCallback callback = (RepositoryCallback) invocation.getArguments()[1];
				Repository repository = Mockito.mock(Repository.class);
				Version version = new Version();
				version.setId(0);
				when(repository.getSourceModelVersion()).thenReturn(version);
				callback.setRepository(repository);
				
				return null;
			}
		}).when(repositoryController).getRepository(Mockito.anyLong(), Mockito.any(RepositoryCallback.class));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Command command = (Command) invocation.getArguments()[2];
				command.execute();
				
				return null;
			}
		}).when(repositoryController).removeRepository(anyLong(), anyLong(), any(Command.class), any(Command.class));
		
		repositoryPanelPresenter.onRemoveRepository();
		
		verify(eventBus).fireEvent(any(RepositoryRemovedEvent.class));
	}
}