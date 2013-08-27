package pl.cyfronet.datanet.test.presenters.entitydata;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.EntityCallback;
import pl.cyfronet.datanet.web.client.di.factory.EntityRowDataProviderFactory;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter.View;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityRowDataProvider;

import com.google.web.bindery.event.shared.EventBus;

public class EntityDataPresenterTest {
	@Mock private View view;
	@Mock private RepositoryController repositoryController;
	@Mock private EntityRowDataProviderFactory entityRowDataProviderFactory;
	@Mock private EventBus eventBus;
	@Mock private EntityRowDataProvider entityRowDataProvider;
	
	private EntityDataPanelPresenter entityDataPanelPresenter;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		entityDataPanelPresenter = new EntityDataPanelPresenter(
				view, repositoryController, 0, "entity", entityRowDataProviderFactory, eventBus);
		when(entityRowDataProviderFactory.create(Mockito.anyLong(), Mockito.anyString(), Mockito.any(EntityDataPanelPresenter.class))).
				thenReturn(entityRowDataProvider);
	}
	
	@Test
	public void testSearchFieldCreation() {
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				EntityCallback entityCallback = (EntityCallback) invocation.getArguments()[2];
				Entity entity = new Entity();
				entity.setFields(new ArrayList<Field>());
				entity.getFields().add(new Field("stringField", Type.String, false));
				entity.getFields().add(new Field("fileField", Type.File, false));
				entityCallback.setEntity(entity);
				
				return null;
			}
		}).when(repositoryController).getEntity(Mockito.anyLong(), Mockito.eq("entity"), Mockito.any(EntityCallback.class));
		entityDataPanelPresenter.show();
		verify(view).addSearchField("stringField", Type.String);
		verify(view, never()).addSearchField("fileField", Type.File);
	}
}