package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter.View;

public class FieldPanelPresenterTest {	

	@Mock
	private EntityPanelPresenter entityPanelPresenter;
	
	@Mock
	private View view;
	
	private FieldPanelPresenter presenter;

	private Field field;

	private String newFiledName = "newFieldName";

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		
		presenter = new FieldPanelPresenter(entityPanelPresenter, view);		
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
		presenter.onFieldNameChanged(newFiledName );		
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

	private void thenTypeChanged() {		
		assertEquals(Type.IntegerArray, presenter.getField().getType());
		thenEntityChanged();
	}

	private void whenFieldTypeChanged() {
		presenter.onFieldTypeChanged("Integer[]");
	}
	
	@Test
	public void should() throws Exception {
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
}
