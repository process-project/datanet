package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.di.factory.EntityPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter.View;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

public class ModelPanelPresenterTest {	

	@Mock
	private View view;
	
	@Mock
	private EventBus eventBus;

	@Mock
	private EntityPanelPresenterFactory entityPanelFactory;
	
	private ModelPanelPresenter presenter;

	private List<String> names;

	private Entity e2;

	private Entity entity;	

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		when(view.getEntityContainer()).thenReturn(mock(HasWidgets.class));		
		
		presenter = new ModelPanelPresenter(view, eventBus, entityPanelFactory);
		
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
		
		Entity e1 = new Entity();
		e1.setName("e1");
		
		e2 = new Entity();
		e2.setName("e2");
		
		Entity e3 = new Entity();
		e3.setName("e3");
		
		m.setEntities(Arrays.asList(e1, e2, e3));
		
		presenter.setModel(new ModelProxy(m));
	}

	private void whenGetModelEntitiesNames() {
		names = presenter.getEntitiesNames();
	}

	private void thenEntitiesNamesReceived() {
		assertEquals(Arrays.asList("e1", "e2", "e3"), names);		
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
}
