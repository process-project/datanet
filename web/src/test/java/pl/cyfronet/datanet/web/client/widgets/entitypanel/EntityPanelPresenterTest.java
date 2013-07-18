package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		when(view.getFieldContainer()).thenReturn(mock(HasWidgets.class));		
		
		presenter = new EntityPanelPresenter(modelPanelPresenter, view, fieldFactory);		
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
}
