package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.test.mock.matcher.ContainsInArrayMatcher;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter.View;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class FieldPanelPresenterTest {

	@Mock
	private EntityPanelPresenter entityPanelPresenter;

	@Mock
	private View view;

	@Mock
	private EventBus eventBus;

	private FieldPanelPresenter presenter;

	private Field field;

	private String newFiledName = "newFieldName";

	private Entity entity;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);

		presenter = new FieldPanelPresenter(entityPanelPresenter, view,
				eventBus);
	}

	@Test
	public void shouldFillView() throws Exception {
		givenField();
		whenSetField();
		thenViewPopulatedUsingFieldData();
	}

	private void givenField() {
		field = new Field();
		field.setName("fieldName");
		field.setRequired(true);
		field.setType(Type.StringArray);
	}

	private void whenSetField() {
		presenter.setField(field);
	}

	private void thenViewPopulatedUsingFieldData() {
		// second time in constructor
		verify(view, times(2)).setRequired(field.isRequired());
		verify(view).setName(field.getName());
		verify(view).selectType("String[]");
	}

	@Test
	public void shouldUpdateFieldName() throws Exception {
		whenFieldNameChange();
		thenNameChanged();
	}

	private void whenFieldNameChange() {
		presenter.onFieldNameChanged(newFiledName);
	}

	private void thenNameChanged() {
		assertEquals(newFiledName, presenter.getField().getName());
		thenEntityChanged();
	}

	private void thenEntityChanged() {
		verify(entityPanelPresenter).entityChanged();
	}

	@Test
	public void shouldUpdateFieldType() throws Exception {
		whenFieldTypeChanged();
		thenTypeChanged();
	}

	private void whenFieldTypeChanged() {
		presenter.onFieldTypeChanged("Integer[]");
	}
	
	private void thenTypeChanged() {
		assertEquals(Type.IntegerArray, presenter.getField().getType());
		thenEntityChanged();
	}

	@Test
	public void shouldUpdateFieldTypeWithReference() throws Exception {
		givenModelWithEntity();
		whenTypeChangedIntoRelation();
		thenRelationIsSetForField();
	}

	private void givenModelWithEntity() {
		entity = new Entity();
		entity.setName("EntityName");

		when(entityPanelPresenter.getEntity(entity.getName())).thenReturn(
				entity);
	}

	private void whenTypeChangedIntoRelation() {
		presenter.onFieldTypeChanged(entity.getName());
	}

	private void thenRelationIsSetForField() {
		assertEquals(Type.ObjectId, presenter.getField().getType());
		assertEquals(entity, presenter.getField().getTarget());
	}

	@Test
	public void shouldUpdateFieldTypeWithReferenceArray() throws Exception {
		givenModelWithEntity();
		whenTypeChangedIntoRelationArray();
		thenRelationArrayIsSetForField();
	}
	
	private void whenTypeChangedIntoRelationArray() {
		presenter.onFieldTypeChanged(entity.getName() + "[]");
	}

	private void thenRelationArrayIsSetForField() {
		assertEquals(Type.ObjectIdArray, presenter.getField().getType());
		assertEquals(entity, presenter.getField().getTarget());
	}

	@Test
	public void shouldUpdateRequired() throws Exception {
		whenFieldRequiredChanged();
		thenRequiredChanged();
	}

	private void whenFieldRequiredChanged() {
		presenter.onFieldRequiredChanged(true);
	}

	private void thenRequiredChanged() {
		assertEquals(true, presenter.getField().isRequired());
		thenEntityChanged();
	}

	@Test
	public void shouldRemoveField() throws Exception {
		whenRemovingField();
		thenFieldRemovedFromEntity();
	}

	private void whenRemovingField() {
		presenter.onRemoveField();
	}

	private void thenFieldRemovedFromEntity() {
		verify(entityPanelPresenter).removeField(presenter);
	}

	@Test
	public void shouldUpdateTypesWhenNewEntityAppears() throws Exception {
		givenEntitesNames();
		whenCreateNewEntity();
		thenTypesListUpdated();
	}

	private void givenEntitesNames() {
		when(entityPanelPresenter.getEntitiesNames()).thenReturn(
				Arrays.asList("newEntity"));
	}

	private void whenCreateNewEntity() {
		presenter.onModelChanged(new ModelChangedEvent(1l));
	}

	private void thenTypesListUpdated() {
		Matcher<List<String>> mather = new ContainsInArrayMatcher("newEntity",
				"newEntity[]");
		verify(view).setTypes(argThat(mather));
	}
}
