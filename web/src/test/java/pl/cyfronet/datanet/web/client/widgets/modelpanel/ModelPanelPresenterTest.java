package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionsCallback;
import pl.cyfronet.datanet.web.client.di.factory.EntityPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.mvp.place.VersionPlace;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter.View;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

public class ModelPanelPresenterTest {
	@Mock private View view;
	@Mock private EventBus eventBus;
	@Mock private EntityPanelPresenterFactory entityPanelFactory;
	@Mock private ModelController modelController;
	@Mock private VersionController versionController;
	@Mock private PlaceController placeController;
	
	private ModelPanelPresenter presenter;
	private List<String> names;
	private Entity e2;
	private Entity entity;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		when(view.getEntityContainer()).thenReturn(mock(HasWidgets.class));

		presenter = new ModelPanelPresenter(view, eventBus, entityPanelFactory,
				modelController, versionController, placeController);

		Model model = new Model();
		model.setId(1l);
		presenter.setModel(new ModelProxy(model));
		
		EntityPanelPresenter epPresenter = mock(EntityPanelPresenter.class);
		when(epPresenter.getWidget()).thenReturn(mock(IsWidget.class));
		when(entityPanelFactory.create(presenter)).thenReturn(epPresenter);
	}

	@Test
	public void shouldGetEntitiesNames() throws Exception {
		givenModelWithEntities();
		whenGetModelEntitiesNames();
		thenEntitiesNamesReceived();
	}

	private void givenModelWithEntities() {
		Model m = new Model();
		m.setName("name");

		m.setEntities(get3Entities());
		e2 = m.getEntities().get(1);
		
		presenter.setModel(new ModelProxy(m));
	}	
	
	private List<Entity> get3Entities() {
		List<Entity> entities = new ArrayList<>();

		Entity e1 = new Entity();
		e1.setName("e1");
		entities.add(e1);
		
		Entity e2 = new Entity();
		e2.setName("e2");
		entities.add(e2);

		Entity e3 = new Entity();
		e3.setName("e3");
		entities.add(e3);
		
		return entities;
	}
	
	private void whenGetModelEntitiesNames() {
		names = presenter.getEntitiesNames();
	}

	private void thenEntitiesNamesReceived() {
		assertEquals(Arrays.asList("e1", "e2", "e3"), names);
	}

	@Test
	public void shouldNotReturnEmptyEntityName() throws Exception {
		givenModelWithEntityWithEmptyName();
		whenGetModelEntitiesNames();
		thenEntitiesNamesReceived();
	}

	private void givenModelWithEntityWithEmptyName() {
		givenModelWithEntities();

		Entity withEmptyName = new Entity();
		withEmptyName.setName("");

		Entity withNullName = new Entity();

		// new list need to be created because previous one is only readonly
		List<Entity> entities = new ArrayList<>(presenter.getModel()
				.getEntities());
		entities.add(withEmptyName);
		entities.add(withNullName);

		presenter.getModel().setEntities(entities);
	}

	@Test
	public void shouldGetEntityByName() throws Exception {
		givenModelWithEntities();
		whenGetEntityE2();
		thenEntityE2Received();
	}

	private void whenGetEntityE2() {
		entity = presenter.getEntity(e2.getName());
	}

	private void thenEntityE2Received() {
		assertEquals(e2, entity);
	}

	@Test
	public void shouldGetNullWhenEntityNotFound() throws Exception {
		givenModelWithEntities();
		whenGetNonExistingEntity();
		thenNullEntityReceived();
	}

	private void whenGetNonExistingEntity() {
		presenter.getEntity("nonexisting");
	}

	private void thenNullEntityReceived() {
		assertNull(entity);
	}
	
	@Test
	public void testNewVersionEmptyName() {
		when(view.getNewVersionText()).thenReturn(new HasText() {
			@Override
			public void setText(String text) {
			}
			
			@Override
			public String getText() {
				return "";
			}
		});
		
		presenter.onCreateNewVersion();
		
		verify(view).setNewVersionErrorState(true);
	}
	
	@Test
	public void testNewVersionNameTooLong() {
		when(view.getNewVersionText()).thenReturn(new HasText() {
			@Override
			public void setText(String text) {
			}
			
			@Override
			public String getText() {
				//name too long
				return "12345678901234567890!";
			}
		});
		
		presenter.onCreateNewVersion();
		
		verify(view).setNewVersionErrorState(true);
	}
	
	@Test
	public void testNewVersionNameExists() {
		final Model model = new Model();
		model.setName("model");
		presenter.setModel(new ModelProxy(model));
		
		when(view.getNewVersionText()).thenReturn(new HasText() {
			@Override
			public void setText(String text) {
			}
			
			@Override
			public String getText() {
				return "nameExists";
			}
		});
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				VersionsCallback versionsCallback = (VersionsCallback) invocation.getArguments()[1];
				versionsCallback.setVersions(Arrays.asList(new Version(model, "nameExists")));
				return null;
			}
		}).when(versionController).getVersions(Mockito.anyLong(), Mockito.any(VersionsCallback.class), Mockito.anyBoolean());
		
		presenter.onCreateNewVersion();
		
		Mockito.verify(view).setNewVersionErrorState(true);
	}
	
	@Test
	public void testNewVersionValidName() {
		final Model model = new Model();
		model.setName("model");
		presenter.setModel(new ModelProxy(model));
		when(view.getNewVersionText()).thenReturn(new HasText() {
			@Override
			public void setText(String text) {
			}
			
			@Override
			public String getText() {
				return "nameDoesNotExist";
			}
		});
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				VersionsCallback versionsCallback = (VersionsCallback) invocation.getArguments()[1];
				versionsCallback.setVersions(Arrays.asList(new Version(model, "nameExists")));
				return null;
			}
		}).when(versionController).getVersions(Mockito.anyLong(), Mockito.any(VersionsCallback.class), Mockito.anyBoolean());
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				VersionCallback versionCallback = (VersionCallback) invocation.getArguments()[2];
				
				Version version = new Version();
				version.setId(0);
				versionCallback.setVersion(version);
				
				return null;
			}
		}).when(versionController).releaseNewVersion(Mockito.anyLong(), Mockito.anyString(), Mockito.any(VersionCallback.class));;
		
		presenter.onCreateNewVersion();
		
		verify(view).setNewVersionErrorState(false);
		verify(view).setNewVersionBusyState(true);
		verify(view).setNewVersionBusyState(false);
		verify(view).hideNewVersionModal();
		verify(placeController).goTo(Mockito.any(VersionPlace.class));
	}
	
	@Test
	public void shouldRemoveModel() throws Exception {
		 givenModelToRemove();
		 whenRemoveModel();
		 thenModelRemoved();
	}

	private void givenModelToRemove() {
		when(view.confirmModelRemoval()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Command successCallback = (Command)invocation.getArguments()[1];
				successCallback.execute();
				return null;
			}
		}).when(modelController).deleteModel(eq(1l), any(Command.class), any(Command.class));
	}

	private void whenRemoveModel() {
		presenter.onDelete();
	}

	private void thenModelRemoved() {
		verify(placeController).goTo(any(WelcomePlace.class));
	}
	
	@Test
	public void shouldNotRemoveModelWhenUserClicksCancel() throws Exception {
		 givenUserClicksCancel();
		 whenRemoveModel();
		 thenModelNotRemoved();
	}	

	private void givenUserClicksCancel() {
		when(view.confirmModelRemoval()).thenReturn(false);
	}
	
	private void thenModelNotRemoved() {
		verify(modelController, times(0)).deleteModel(eq(1l), any(Command.class), any(Command.class));
		thenRemoveButtonReseted();
	}
	
	private void thenRemoveButtonReseted() {
		verify(view).resetRemoveButton();		
	}
	
	@Test
	public void shouldResetRemoveButtonWhenRemoveError() throws Exception {
		 givenRemoveError();
		 whenRemoveModel();
		 thenRemoveButtonReseted();
	}

	private void givenRemoveError() {
		when(view.confirmModelRemoval()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Command errorCallback = (Command)invocation.getArguments()[2];
				errorCallback.execute();
				return null;
			}
		}).when(modelController).deleteModel(eq(1l), any(Command.class), any(Command.class));		
	}	
	
	@Test
	public void shouldSaveModel() throws Exception {
		 givenModelToSave();
		 whenSaveModel();
		 thenModelSaved();
	}

	private void givenModelToSave() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelCallback successCallback = (ModelCallback)invocation.getArguments()[1];
				successCallback.setModel(new ModelProxy(new Model()));
				return null;
			}
		}).when(modelController).saveModel(eq(1l), any(ModelCallback.class));
	}

	private void whenSaveModel() {
		presenter.onSave();
	}

	private void thenModelSaved() {
//		verify(view).setSaving();
//		verify(view).resetSaveButton();
		verify(view, times(2)).setSaveEnabled(false);
	}
	
	@Test
	public void shouldConfirmSaveBeforeVersionCreationIfModelIsDirty() {
		when(view.confirmSaveBeforeVersion()).thenReturn(true);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelCallback modelCallback = (ModelCallback) invocation.getArguments()[1];
				modelCallback.setModel(new ModelProxy(new Model()));
				
				return null;
			}
			
		}).when(modelController).saveModel(any(Long.class), any(ModelCallback.class));;
		
		ModelProxy modelProxy = new ModelProxy(new Model());
		modelProxy.setDirty(true);
		presenter.setModel(modelProxy);
		
		presenter.onNewVersionModal();
		
		verify(view).confirmSaveBeforeVersion();
		verify(modelController).saveModel(any(Long.class), any(ModelCallback.class));
		verify(view).showNewVersionModal();
	}
}