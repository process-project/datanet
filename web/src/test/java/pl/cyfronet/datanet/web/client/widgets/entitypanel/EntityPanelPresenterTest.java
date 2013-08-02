package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.web.client.di.factory.FieldPanelPresenterFactory;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter.View;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class EntityPanelPresenterTest {

	@Mock
	private ModelPanelPresenter modelPanelPresenter;

	@Mock
	private View view;

	private EntityPanelPresenter presenter;

	@Mock
	private FieldPanelPresenterFactory fieldFactory;

	@Mock
	private FieldPanelPresenter fieldPrsenter;

	private Field field;

	private Entity entity;

	private List<String> names;

	private Object receivedEntity;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		when(view.getFieldContainer()).thenReturn(mock(HasWidgets.class));

		presenter = new EntityPanelPresenter(modelPanelPresenter, view,
				fieldFactory);
	}

	@Test
	public void shouldRemoveEntityField() throws Exception {
		givenEntityWithField();
		whenRemoveField();
		thenFieldRemovedFromViewAndModel();
	}

	private void givenEntityWithField() {
		entity = new Entity();
		entity.getFields().add(field);

		when(fieldFactory.create(presenter)).thenReturn(fieldPrsenter);
		when(fieldPrsenter.getField()).thenReturn(field);
		when(fieldPrsenter.getWidget()).thenReturn(mock(IsWidget.class));

		presenter.setEntity(entity);
	}

	private void whenRemoveField() {
		presenter.removeField(fieldPrsenter);
	}

	private void thenFieldRemovedFromViewAndModel() {
		assertEquals(0, entity.getFields().size());
	}

	@Test
	public void shouldGetEntitiesNames() throws Exception {
		givenEntitiesNames();
		whenGetEntitiesNames();
		thenEntitiesNamesReceivedFromModelPresenter();
	}

	private void givenEntitiesNames() {
		when(modelPanelPresenter.getEntitiesNames()).thenReturn(
				Arrays.asList("e1", "e2", "e3"));
	}

	private void whenGetEntitiesNames() {
		names = modelPanelPresenter.getEntitiesNames();
	}

	private void thenEntitiesNamesReceivedFromModelPresenter() {
		assertEquals(Arrays.asList("e1", "e2", "e3"), names);
	}

	@Test
	public void shouldGetEntityByName() throws Exception {
		givenEntityInModel();
		whenGetEntityByName();
		thenEntityReceived();
	}

	private void givenEntityInModel() {
		entity = new Entity();
		entity.setName("e1");
		when(modelPanelPresenter.getEntity(entity.getName()))
				.thenReturn(entity);
	}

	private void whenGetEntityByName() {
		receivedEntity = presenter.getEntity(entity.getName());
	}

	private void thenEntityReceived() {
		assertEquals(entity, receivedEntity);
	}
}
